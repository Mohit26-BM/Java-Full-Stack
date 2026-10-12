package com.company.jdbc.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.company.jdbc.config.DBConnection;
import com.company.jdbc.dao.CustomerDao;
import com.company.jdbc.dao.OrderDao;
import com.company.jdbc.dao.OrderItemDao;
import com.company.jdbc.dao.ProductDao;
import com.company.jdbc.exception.CustomerNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.OrderAlreadyCancelledException;
import com.company.jdbc.exception.OrderFailedException;
import com.company.jdbc.exception.OrderNotFoundException;
import com.company.jdbc.exception.ProductNotFoundException;
import com.company.jdbc.model.Order;
import com.company.jdbc.model.OrderItem;
import com.company.jdbc.model.OrderItemRequest;
import com.company.jdbc.model.OrderStatus;
import com.company.jdbc.model.Product;

/*
placeOrder() is ONE transaction:

    1. check the customer exists
    2. INSERT the order header         -> getGeneratedKeys() = new order_id
    3. for each product:
         lock the product row          (SELECT ... FOR UPDATE)
         verify stock
         INSERT the order item         (uses the generated order_id)
         reduce stock                  (UPDATE)
         add quantity x price to the total
    4. UPDATE the order total
    5. COMMIT

If ANY step fails -> ROLLBACK. The order header, the items already
inserted and the stock already reduced are all undone, so no
partial order can remain.

Note: MySQL does not give back an AUTO_INCREMENT number after a
rollback, so order IDs can have gaps. That is normal.
*/
public class OrderServiceImpl implements OrderService {

    /*
    Largest value that fits in DECIMAL(12,2).
    */
    private static final BigDecimal MAX_TOTAL = new BigDecimal("9999999999.99");

    private final CustomerDao customerDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public OrderServiceImpl(CustomerDao customerDao,
                            ProductDao productDao,
                            OrderDao orderDao,
                            OrderItemDao orderItemDao) {

        this.customerDao = customerDao;
        this.productDao = productDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    @Override
    public long placeOrder(long customerId, List<OrderItemRequest> items)
            throws InvalidInputException,
                   CustomerNotFoundException,
                   ProductNotFoundException,
                   InsufficientStockException,
                   OrderFailedException {

        return executePlaceOrder(customerId, items, false);
    }

    @Override
    public long placeOrderWithSimulatedFailure(long customerId, List<OrderItemRequest> items)
            throws InvalidInputException,
                   CustomerNotFoundException,
                   ProductNotFoundException,
                   InsufficientStockException,
                   OrderFailedException {

        return executePlaceOrder(customerId, items, true);
    }

    // =========================================================
    // PLACE ORDER - THE TRANSACTION
    // =========================================================

    private long executePlaceOrder(long customerId,
                                   List<OrderItemRequest> items,
                                   boolean failBeforeCommit)
            throws InvalidInputException,
                   CustomerNotFoundException,
                   ProductNotFoundException,
                   InsufficientStockException,
                   OrderFailedException {

        /*
        product_id -> total quantity, sorted by product_id.
        */
        Map<Long, Integer> quantities = mergeItems(items);

        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                // ---------- Step 1: customer ----------

                if (customerDao.findById(connection, customerId) == null) {
                    throw new CustomerNotFoundException(
                            "Customer with ID " + customerId + " was not found."
                    );
                }

                // ---------- Step 2: order header ----------

                long orderId = orderDao.insert(connection, customerId, OrderStatus.PLACED);

                BigDecimal total = BigDecimal.ZERO;

                // ---------- Step 3: items ----------

                /*
                Products are processed in product_id order. Two orders that
                share products therefore always lock rows in the same order,
                which prevents deadlocks.
                */
                for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {

                    long productId = entry.getKey();
                    int quantity = entry.getValue();

                    Product product = productDao.findByIdForUpdate(connection, productId);

                    if (product == null) {
                        throw new ProductNotFoundException(
                                "Product with ID " + productId + " was not found."
                        );
                    }

                    if (product.getStockQty() < quantity) {
                        throw new InsufficientStockException(
                                "Not enough stock for '" + product.getProductName()
                                + "' (ID " + productId + "): requested "
                                + quantity + ", available " + product.getStockQty() + "."
                        );
                    }

                    /*
                    The SAME generated orderId is used for every item.
                    The price is copied from the product at this moment.
                    */
                    OrderItem item = new OrderItem(
                            orderId, productId, quantity, product.getUnitPrice());

                    orderItemDao.insert(connection, item);

                    if (!productDao.decreaseStock(connection, productId, quantity)) {
                        throw new SQLException(
                                "Stock for product " + productId + " changed unexpectedly."
                        );
                    }

                    total = total.add(item.getLineTotal());
                }

                if (total.compareTo(MAX_TOTAL) > 0) {
                    throw new InvalidInputException(
                            "Order total Rs. " + total + " is too large."
                    );
                }

                if (failBeforeCommit) {

                    /*
                    Deliberate failure AFTER the header, all items and all
                    stock reductions were written. Rollback must undo them all.
                    */
                    throw new IllegalStateException(
                            "Simulated failure after inserting order " + orderId
                            + " and its items (before commit)."
                    );
                }

                // ---------- Step 4: total ----------

                orderDao.updateTotal(connection, orderId, total);

                // ---------- Step 5: commit ----------

                connection.commit();

                return orderId;

            } catch (CustomerNotFoundException
                     | ProductNotFoundException
                     | InsufficientStockException
                     | InvalidInputException e) {

                rollbackQuietly(connection, e);
                throw e;

            } catch (SQLException | RuntimeException e) {

                rollbackQuietly(connection, e);

                throw new OrderFailedException(
                        "Order failed and was ROLLED BACK. Nothing was saved "
                        + "and no stock was changed. Reason: " + e.getMessage(),
                        e
                );

            } finally {

                restoreAutoCommit(connection, originalAutoCommit);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to connect to the database", e);
        }
    }

    /*
    Validates the request and combines duplicate lines,
    e.g. [mouse x2, keyboard x1, mouse x3] -> {mouse: 5, keyboard: 1}.
    TreeMap keeps the products sorted by ID (fixed lock order).
    */
    private Map<Long, Integer> mergeItems(List<OrderItemRequest> items)
            throws InvalidInputException {

        if (items == null || items.isEmpty()) {
            throw new InvalidInputException("An order must contain at least one item.");
        }

        Map<Long, Integer> quantities = new TreeMap<>();

        for (OrderItemRequest item : items) {

            if (item.getQuantity() <= 0) {
                throw new InvalidInputException(
                        "Quantity for product " + item.getProductId()
                        + " must be greater than 0."
                );
            }

            Integer existing = quantities.get(item.getProductId());

            try {

                int combined = existing == null
                        ? item.getQuantity()
                        : Math.addExact(existing, item.getQuantity());

                quantities.put(item.getProductId(), combined);

            } catch (ArithmeticException e) {

                throw new InvalidInputException(
                        "Quantity for product " + item.getProductId() + " is too large."
                );
            }
        }

        return quantities;
    }

    // =========================================================
    // ORDER DETAILS
    // =========================================================

    @Override
    public Order getOrderDetails(long orderId)
            throws OrderNotFoundException {

        Order order = orderDao.findDetails(orderId);

        if (order == null) {
            throw new OrderNotFoundException(
                    "Order with ID " + orderId + " was not found."
            );
        }

        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    @Override
    public int countOrders() {
        return orderDao.countAll();
    }

    @Override
    public int countOrderItems() {
        return orderItemDao.countAll();
    }

    // =========================================================
    // CANCEL ORDER - OPTIONAL CHALLENGE
    // =========================================================

    @Override
    public void cancelOrder(long orderId)
            throws OrderNotFoundException,
                   OrderAlreadyCancelledException,
                   OrderFailedException {

        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                /*
                Lock the order row first. A second cancel request for the
                same order waits here, then sees status = CANCELLED.
                */
                Order order = orderDao.findByIdForUpdate(connection, orderId);

                if (order == null) {
                    throw new OrderNotFoundException(
                            "Order with ID " + orderId + " was not found."
                    );
                }

                if (order.getStatus() == OrderStatus.CANCELLED) {
                    throw new OrderAlreadyCancelledException(
                            "Order " + orderId + " is already cancelled. "
                            + "Stock was not restored again."
                    );
                }

                /*
                Put every item's quantity back into stock
                (items come back sorted by product_id).
                */
                for (OrderItem item : orderItemDao.findByOrderId(connection, orderId)) {
                    productDao.increaseStock(connection, item.getProductId(), item.getQuantity());
                }

                /*
                Second safety net: only changes PLACED -> CANCELLED.
                */
                if (!orderDao.markCancelled(connection, orderId)) {
                    throw new OrderAlreadyCancelledException(
                            "Order " + orderId + " is already cancelled."
                    );
                }

                connection.commit();

            } catch (OrderNotFoundException | OrderAlreadyCancelledException e) {

                rollbackQuietly(connection, e);
                throw e;

            } catch (SQLException | RuntimeException e) {

                rollbackQuietly(connection, e);

                throw new OrderFailedException(
                        "Cancellation failed and was ROLLED BACK. Reason: " + e.getMessage(),
                        e
                );

            } finally {

                restoreAutoCommit(connection, originalAutoCommit);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Unable to connect to the database", e);
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void rollbackQuietly(Connection connection, Exception cause) {

        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }

    private void restoreAutoCommit(Connection connection, boolean original) {

        try {
            connection.setAutoCommit(original);
        } catch (SQLException ignored) {
            // The connection is closed right after this anyway.
        }
    }
}
