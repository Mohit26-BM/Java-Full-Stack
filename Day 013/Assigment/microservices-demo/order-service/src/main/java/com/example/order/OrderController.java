package com.example.order;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final ProductClient productClient;
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody OrderRequest request) {
        if (request.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }
        int quantity = request.quantity() == null ? 1 : request.quantity();
        if (quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be at least 1");
        }

        Product product = productClient.getProduct(request.productId());

        String status = product.instance() == null ? "PENDING" : "CREATED";
        Order order = new Order(nextId.getAndIncrement(), request.productId(), quantity, product,
                product.price() * quantity, status);
        orders.put(order.id(), order);
        log.info("Order {} {} for product {} (product instance {})", order.id(), status, product.id(), product.instance());
        return order;
    }

    @GetMapping
    public Collection<Order> all() {
        return orders.values();
    }

    @GetMapping("/{id}")
    public Order one(@PathVariable Long id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order " + id + " not found");
        }
        return order;
    }
}
