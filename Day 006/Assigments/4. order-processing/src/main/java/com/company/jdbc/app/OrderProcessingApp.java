package com.company.jdbc.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.company.jdbc.dao.CustomerDao;
import com.company.jdbc.dao.CustomerDaoImpl;
import com.company.jdbc.dao.OrderDao;
import com.company.jdbc.dao.OrderDaoImpl;
import com.company.jdbc.dao.OrderItemDao;
import com.company.jdbc.dao.OrderItemDaoImpl;
import com.company.jdbc.dao.ProductDao;
import com.company.jdbc.dao.ProductDaoImpl;
import com.company.jdbc.exception.CustomerNotFoundException;
import com.company.jdbc.exception.DataAccessException;
import com.company.jdbc.exception.DuplicateCustomerException;
import com.company.jdbc.exception.InsufficientStockException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.exception.OrderAlreadyCancelledException;
import com.company.jdbc.exception.OrderFailedException;
import com.company.jdbc.exception.OrderNotFoundException;
import com.company.jdbc.exception.ProductNotFoundException;
import com.company.jdbc.model.Customer;
import com.company.jdbc.model.Order;
import com.company.jdbc.model.OrderItem;
import com.company.jdbc.model.OrderItemRequest;
import com.company.jdbc.model.Product;
import com.company.jdbc.service.CustomerService;
import com.company.jdbc.service.CustomerServiceImpl;
import com.company.jdbc.service.OrderService;
import com.company.jdbc.service.OrderServiceImpl;
import com.company.jdbc.service.ProductService;
import com.company.jdbc.service.ProductServiceImpl;

public class OrderProcessingApp {

    private static final String LINE =
            "------------------------------------------------------------------------------";

    private static CustomerService customerService;
    private static ProductService productService;
    private static OrderService orderService;

    public static void main(String[] args) {

        /*
        Manual dependency injection: DAO -> Service.
        */
        CustomerDao customerDao = new CustomerDaoImpl();
        ProductDao productDao = new ProductDaoImpl();
        OrderDao orderDao = new OrderDaoImpl();
        OrderItemDao orderItemDao = new OrderItemDaoImpl();

        customerService = new CustomerServiceImpl(customerDao);
        productService = new ProductServiceImpl(productDao);
        orderService = new OrderServiceImpl(customerDao, productDao, orderDao, orderItemDao);

        while (true) {

            showMenu();

            int choice = ConsoleInput.readPositiveInt("Enter your choice: ");

            if (choice == 12) {
                System.out.println("\nThank you for using the Order Processing System.");
                return;
            }

            try {

                handleChoice(choice);

            } catch (DataAccessException e) {

                System.out.println("\nDatabase Error: " + e.getMessage());

                if (e.getCause() != null) {
                    System.out.println("Cause: " + e.getCause().getMessage());
                }

            } catch (IllegalStateException e) {

                System.out.println("\nConfiguration Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {

        System.out.println("\n========================================");
        System.out.println("     E-COMMERCE ORDER PROCESSING");
        System.out.println("========================================");
        System.out.println(" CUSTOMERS");
        System.out.println("  1. Add Customer");
        System.out.println("  2. View Customers");
        System.out.println(" PRODUCTS");
        System.out.println("  3. Add Product");
        System.out.println("  4. View Products");
        System.out.println("  5. Update Product Price");
        System.out.println("  6. Add Product Stock");
        System.out.println(" ORDERS");
        System.out.println("  7. Place Order");
        System.out.println("  8. Order Details");
        System.out.println("  9. View All Orders");
        System.out.println(" 10. Cancel Order");
        System.out.println(" 11. Rollback Demo (fail before commit)");
        System.out.println(" 12. Exit");
        System.out.println("========================================");
    }

    private static void handleChoice(int choice) {

        switch (choice) {
            case 1:  addCustomer();     break;
            case 2:  viewCustomers();   break;
            case 3:  addProduct();      break;
            case 4:  viewProducts();    break;
            case 5:  updatePrice();     break;
            case 6:  addStock();        break;
            case 7:  placeOrder(false); break;
            case 8:  orderDetails();    break;
            case 9:  viewAllOrders();   break;
            case 10: cancelOrder();     break;
            case 11: placeOrder(true);  break;
            default:
                System.out.println("\nInvalid choice. Please select 1 to 12.");
        }
    }

    // =========================================================
    // CUSTOMERS
    // =========================================================

    private static void addCustomer() {

        System.out.println("\n========== Add Customer ==========");

        String name = ConsoleInput.readLine("Name: ");
        String email = ConsoleInput.readLine("Email: ");

        try {

            long customerId = customerService.addCustomer(name, email);

            System.out.println("\nCustomer added. Customer ID: " + customerId);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (DuplicateCustomerException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void viewCustomers() {

        System.out.println("\n========== Customers ==========");

        printCustomers(customerService.getAllCustomers());
    }

    // =========================================================
    // PRODUCTS
    // =========================================================

    private static void addProduct() {

        System.out.println("\n========== Add Product ==========");

        String name = ConsoleInput.readLine("Product name: ");
        BigDecimal price = ConsoleInput.readMoney("Unit price: Rs. ");
        int stock = ConsoleInput.readNonNegativeInt("Opening stock: ");

        try {

            long productId = productService.addProduct(name, price, stock);

            System.out.println("\nProduct added. Product ID: " + productId);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
        }
    }

    private static void viewProducts() {

        System.out.println("\n========== Products ==========");

        printProducts(productService.getAllProducts());
    }

    private static void updatePrice() {

        System.out.println("\n========== Update Product Price ==========");

        long productId = ConsoleInput.readPositiveLong("Product ID: ");

        try {

            Product product = productService.getProduct(productId);

            System.out.println("Current price of '" + product.getProductName()
                    + "': Rs. " + product.getUnitPrice());

            BigDecimal price = ConsoleInput.readMoney("New price: Rs. ");

            productService.updatePrice(productId, price);

            System.out.println("\nPrice updated. Existing orders keep their old price.");

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (ProductNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void addStock() {

        System.out.println("\n========== Add Product Stock ==========");

        long productId = ConsoleInput.readPositiveLong("Product ID: ");
        int quantity = ConsoleInput.readPositiveInt("Quantity to add: ");

        try {

            productService.addStock(productId, quantity);

            System.out.println("\nStock updated. New stock: "
                    + productService.getProduct(productId).getStockQty());

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());

        } catch (ProductNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    // =========================================================
    // ORDERS
    // =========================================================

    /*
    Used for both "Place Order" and "Rollback Demo".
    Takes a snapshot BEFORE the order and compares it AFTER,
    to show the acceptance tests in action.
    */
    private static void placeOrder(boolean simulateFailure) {

        System.out.println(simulateFailure
                ? "\n========== Rollback Demo =========="
                : "\n========== Place Order ==========");

        if (simulateFailure) {
            System.out.println("The order header, every item and every stock reduction");
            System.out.println("will be written, then a failure happens before COMMIT.");
        }

        printCustomers(customerService.getAllCustomers());
        long customerId = ConsoleInput.readPositiveLong("Customer ID: ");

        printProducts(productService.getAllProducts());
        System.out.println("Add items. Leave Product ID blank to finish.");

        List<OrderItemRequest> items = new ArrayList<>();

        while (true) {

            Long productId = ConsoleInput.readOptionalPositiveLong("\nProduct ID: ");

            if (productId == null) {
                break;
            }

            int quantity = ConsoleInput.readPositiveInt("Quantity: ");

            items.add(new OrderItemRequest(productId, quantity));
        }

        if (items.isEmpty()) {
            System.out.println("No items entered. Order not placed.");
            return;
        }

        // ---------- snapshot BEFORE ----------

        Map<Long, Integer> requested = requestedQuantities(items);
        Map<Long, Integer> stockBefore = readStock(requested);
        int ordersBefore = orderService.countOrders();
        int itemsBefore = orderService.countOrderItems();

        try {

            long orderId = simulateFailure
                    ? orderService.placeOrderWithSimulatedFailure(customerId, items)
                    : orderService.placeOrder(customerId, items);

            System.out.println("\nOrder PLACED and COMMITTED. Generated order ID: " + orderId);

            Order order = orderService.getOrderDetails(orderId);

            printOrderDetails(order);
            verifySuccessfulOrder(order, requested, stockBefore);

        } catch (InvalidInputException e) {

            System.out.println("\nValidation Error: " + e.getMessage());
            verifyNothingChanged(stockBefore, ordersBefore, itemsBefore);

        } catch (CustomerNotFoundException
                 | ProductNotFoundException
                 | InsufficientStockException e) {

            System.out.println("\nOrder REJECTED and rolled back: " + e.getMessage());
            verifyNothingChanged(stockBefore, ordersBefore, itemsBefore);

        } catch (OrderFailedException e) {

            System.out.println("\n" + e.getMessage());
            verifyNothingChanged(stockBefore, ordersBefore, itemsBefore);

        } catch (OrderNotFoundException e) {

            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void orderDetails() {

        System.out.println("\n========== Order Details ==========");

        long orderId = ConsoleInput.readPositiveLong("Order ID: ");

        try {

            printOrderDetails(orderService.getOrderDetails(orderId));

        } catch (OrderNotFoundException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    private static void viewAllOrders() {

        System.out.println("\n========== All Orders ==========");

        List<Order> orders = orderService.getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }

        System.out.println(LINE);
        System.out.println(String.format("%-6s %-20s %-19s %-10s %14s",
                "Order", "Customer", "Date", "Status", "Total"));
        System.out.println(LINE);

        for (Order order : orders) {
            System.out.println(order);
        }

        System.out.println(LINE);
    }

    private static void cancelOrder() {

        System.out.println("\n========== Cancel Order ==========");

        long orderId = ConsoleInput.readPositiveLong("Order ID: ");

        try {

            Order order = orderService.getOrderDetails(orderId);

            printOrderDetails(order);

            if (!ConsoleInput.readYes("\nCancel this order and restore stock? (y/n): ")) {
                System.out.println("Cancellation aborted.");
                return;
            }

            Map<Long, Integer> stockBefore = new LinkedHashMap<>();

            for (OrderItem item : order.getItems()) {
                stockBefore.put(item.getProductId(), stockOf(item.getProductId()));
            }

            orderService.cancelOrder(orderId);

            System.out.println("\nOrder " + orderId + " CANCELLED. Stock restored:");
            System.out.println("-------------------------");

            for (OrderItem item : order.getItems()) {

                System.out.println(String.format("%-30.30s %4d -> %4d  (+%d)",
                        item.getProductName(),
                        stockBefore.get(item.getProductId()),
                        stockOf(item.getProductId()),
                        item.getQuantity()));
            }

            System.out.println("-------------------------");
            System.out.println("Try cancelling order " + orderId
                    + " again: it will be refused.");

        } catch (OrderNotFoundException | OrderAlreadyCancelledException e) {

            System.out.println("\n" + e.getMessage());

        } catch (OrderFailedException e) {

            System.out.println("\n" + e.getMessage());
        }
    }

    // =========================================================
    // VERIFICATION (acceptance tests)
    // =========================================================

    private static void verifySuccessfulOrder(Order order,
                                              Map<Long, Integer> requested,
                                              Map<Long, Integer> stockBefore) {

        System.out.println("\nChecks:");

        // Generated order ID is used for all order items
        boolean sameOrderId = true;

        for (OrderItem item : order.getItems()) {
            if (item.getOrderId() != order.getOrderId()) {
                sameOrderId = false;
            }
        }

        System.out.println((sameOrderId ? "[OK] " : "[PROBLEM] ")
                + "All " + order.getItems().size()
                + " item(s) use order ID " + order.getOrderId());

        // Stock is reduced exactly by ordered quantity
        for (Map.Entry<Long, Integer> entry : requested.entrySet()) {

            Integer before = stockBefore.get(entry.getKey());
            Integer after = stockOf(entry.getKey());
            boolean exact = before != null && after != null
                    && before - after == entry.getValue();

            System.out.println((exact ? "[OK] " : "[PROBLEM] ")
                    + "Product " + entry.getKey() + " stock " + before + " -> " + after
                    + " (ordered " + entry.getValue() + ")");
        }

        // Order total equals the sum of item totals
        BigDecimal itemsTotal = order.calculateItemsTotal();
        boolean totalMatches = itemsTotal.compareTo(order.getTotalAmount()) == 0;

        System.out.println((totalMatches ? "[OK] " : "[PROBLEM] ")
                + "Saved total Rs. " + order.getTotalAmount()
                + " = sum of items Rs. " + itemsTotal);
    }

    private static void verifyNothingChanged(Map<Long, Integer> stockBefore,
                                             int ordersBefore,
                                             int itemsBefore) {

        int ordersAfter = orderService.countOrders();
        int itemsAfter = orderService.countOrderItems();

        boolean unchanged = ordersBefore == ordersAfter && itemsBefore == itemsAfter;

        System.out.println("\nChecks after rollback:");
        System.out.println("Orders rows      : " + ordersBefore + " -> " + ordersAfter);
        System.out.println("Order item rows  : " + itemsBefore + " -> " + itemsAfter);

        for (Map.Entry<Long, Integer> entry : stockBefore.entrySet()) {

            Integer after = stockOf(entry.getKey());

            if (entry.getValue() != null) {

                System.out.println("Product " + entry.getKey() + " stock : "
                        + entry.getValue() + " -> " + after);

                if (!entry.getValue().equals(after)) {
                    unchanged = false;
                }
            }
        }

        System.out.println(unchanged
                ? "[OK] No partial order remains; database state is unchanged."
                : "[PROBLEM] Data changed even though the order failed!");
    }

    private static Map<Long, Integer> requestedQuantities(List<OrderItemRequest> items) {

        Map<Long, Integer> requested = new LinkedHashMap<>();

        for (OrderItemRequest item : items) {

            Integer existing = requested.get(item.getProductId());
            requested.put(item.getProductId(),
                    existing == null ? item.getQuantity() : existing + item.getQuantity());
        }

        return requested;
    }

    private static Map<Long, Integer> readStock(Map<Long, Integer> requested) {

        Map<Long, Integer> stock = new LinkedHashMap<>();

        for (Long productId : requested.keySet()) {
            stock.put(productId, stockOf(productId));
        }

        return stock;
    }

    /*
    Current stock, or null when the product does not exist.
    */
    private static Integer stockOf(long productId) {

        try {
            return productService.getProduct(productId).getStockQty();
        } catch (ProductNotFoundException e) {
            return null;
        }
    }

    // =========================================================
    // OUTPUT HELPERS
    // =========================================================

    private static void printOrderDetails(Order order) {

        System.out.println("-------------------------");
        System.out.println("Order ID  : " + order.getOrderId());
        System.out.println("Customer  : " + order.getCustomerName()
                + " <" + order.getCustomerEmail() + ">");
        System.out.println("Date      : "
                + (order.getOrderDate() == null
                        ? "-" : order.getOrderDate().toString().replace('T', ' ')));
        System.out.println("Status    : " + order.getStatus());

        System.out.println(LINE);
        System.out.println(String.format("%-6s %-6s %-30s %5s   %12s   %14s",
                "Item", "Prod", "Product", "Qty", "Unit price", "Line total"));
        System.out.println(LINE);

        for (OrderItem item : order.getItems()) {
            System.out.println(item);
        }

        System.out.println(LINE);
        System.out.println(String.format("%62s %14s", "ORDER TOTAL:",
                "Rs. " + order.getTotalAmount()));
    }

    private static void printCustomers(List<Customer> customers) {

        System.out.println("\nCustomers:");
        System.out.println(String.format("%-4s %-22s %s", "ID", "Name", "Email"));

        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    private static void printProducts(List<Product> products) {

        System.out.println("\nProducts:");
        System.out.println(String.format("%-4s %-30s %14s %7s",
                "ID", "Product", "Unit price", "Stock"));

        for (Product product : products) {
            System.out.println(product);
        }
    }
}
