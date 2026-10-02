import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OrderProcessingSystem {

    public static void main(String[] args) {

        /*
        Creating orders
        */

        OrderProcessingData order1 =
                new OrderProcessingData(
                        "ORD101", "Ravi", "ELECTRONICS",
                        25000, "DELIVERED");

        OrderProcessingData order2 =
                new OrderProcessingData(
                        "ORD102", "Priya", "CLOTHING",
                        15000, "DELIVERED");

        OrderProcessingData order3 =
                new OrderProcessingData(
                        "ORD103", "Amit", "ELECTRONICS",
                        18000, "SHIPPED");

        OrderProcessingData order4 =
                new OrderProcessingData(
                        "ORD104", "Neha", "ELECTRONICS",
                        12000, "CANCELLED");

        OrderProcessingData order5 =
                new OrderProcessingData(
                        "ORD105", "Rahul", "ELECTRONICS",
                        30000, "DELIVERED");

        OrderProcessingData order6 =
                new OrderProcessingData(
                        "ORD106", "Sneha", "BOOKS",
                        8000, "DELIVERED");

        OrderProcessingData order7 =
                new OrderProcessingData(
                        "ORD107", "Karan", "ELECTRONICS",
                        9000, "DELIVERED");

        OrderProcessingData order8 =
                new OrderProcessingData(
                        "ORD108", "Anjali", "ELECTRONICS",
                        22000, "DELIVERED");


        /*
        Predicate 1
        Identifies orders above Rs. 10000.
        */

        Predicate<OrderProcessingData> highValueOrder =
                order -> order.amount > 10000;


        /*
        Predicate 2
        Identifies delivered orders.
        */

        Predicate<OrderProcessingData> deliveredOrder =
                order -> order.status.equals("DELIVERED");


        /*
        Predicate 3
        Identifies cancelled orders.
        */

        Predicate<OrderProcessingData> cancelledOrder =
                order -> order.status.equals("CANCELLED");


        /*
        Predicate 4
        Identifies electronics orders.
        */

        Predicate<OrderProcessingData> electronicsOrder =
                order -> order.category.equals("ELECTRONICS");


        /*
        Combining predicates to identify:
        Delivered electronics orders above Rs. 10000.
        */

        Predicate<OrderProcessingData> matchingOrder =
                highValueOrder
                .and(deliveredOrder)
                .and(electronicsOrder);


        /*
        Function 1
        Order -> Customer Name
        */

        Function<OrderProcessingData, String> customerName =
                order -> order.customerName;


        /*
        Function 2
        Order -> Order Amount
        */

        Function<OrderProcessingData, Double> orderAmount =
                order -> order.amount;


        /*
        Function 3
        Order -> Order Summary
        */

        Function<OrderProcessingData, String> orderSummary =
                order -> order.customerName
                        + " placed an order worth Rs. "
                        + order.amount;


        /*
        Consumer 1
        Displays order information.
        */

        Consumer<OrderProcessingData> displayOrder =
                order -> {

                    System.out.println("Order ID: " + order.orderId);
                    System.out.println("Customer: " + order.customerName);
                    System.out.println("Category: " + order.category);
                    System.out.println("Amount: Rs. " + order.amount);
                    System.out.println("Status: " + order.status);
                };


        /*
        Consumer 2
        Prints an invoice.
        */

        Consumer<OrderProcessingData> printInvoice =
                order -> {

                    System.out.println("INVOICE");
                    System.out.println("Order ID: " + order.orderId);
                    System.out.println("Customer: " + order.customerName);
                    System.out.println("Amount: Rs. " + order.amount);
                };


        /*
        Consumer 3
        Sends a notification.
        */

        Consumer<OrderProcessingData> sendNotification =
                order -> {

                    System.out.println(
                            "Notification sent to "
                            + order.customerName
                            + " for order "
                            + order.orderId
                    );
                };


        /*
        Chain consumers using andThen()
        */

        Consumer<OrderProcessingData> orderProcessing =
                displayOrder
                .andThen(printInvoice)
                .andThen(sendNotification);


        /*
        Supplier
        Generates order IDs starting from ORD1001.
        */

        AtomicInteger orderNumber =
                new AtomicInteger(1001);

        Supplier<String> orderIdSupplier =
                () -> "ORD" + orderNumber.getAndIncrement();


        /*
        Generate three order IDs.
        */

        System.out.println("GENERATED ORDER IDs");

        System.out.println(orderIdSupplier.get());
        System.out.println(orderIdSupplier.get());
        System.out.println(orderIdSupplier.get());

        System.out.println();


        /*
        Processing Pipeline
        Predicate -> Function -> Consumer
        */

        System.out.println(
                "DELIVERED ELECTRONICS ORDERS ABOVE Rs. 10000");

        if (matchingOrder.test(order1)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order1));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order1));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order1));

            orderProcessing.accept(order1);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order2)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order2));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order2));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order2));

            orderProcessing.accept(order2);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order3)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order3));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order3));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order3));

            orderProcessing.accept(order3);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order4)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order4));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order4));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order4));

            orderProcessing.accept(order4);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order5)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order5));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order5));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order5));

            orderProcessing.accept(order5);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order6)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order6));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order6));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order6));

            orderProcessing.accept(order6);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order7)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order7));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order7));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order7));

            orderProcessing.accept(order7);

            System.out.println("-------------------------");
        }


        if (matchingOrder.test(order8)) {

            System.out.println(
                    "Customer: "
                    + customerName.apply(order8));

            System.out.println(
                    "Amount: Rs. "
                    + orderAmount.apply(order8));

            System.out.println(
                    "Summary: "
                    + orderSummary.apply(order8));

            orderProcessing.accept(order8);

            System.out.println("-------------------------");
        }
    }
}