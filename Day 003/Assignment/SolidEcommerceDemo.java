/*
SOLID PRINCIPLES MASTER CASE STUDY

This program demonstrates:

1. Single Responsibility Principle
2. Open/Closed Principle
3. Liskov Substitution Principle
4. Interface Segregation Principle
5. Dependency Inversion Principle
*/


/*
SINGLE RESPONSIBILITY PRINCIPLE

Order class is responsible only for storing order information.
It does not send emails, generate invoices or create reports.
*/

class EcommerceOrder {

    private int orderId;
    private String customerName;
    private double amount;

    EcommerceOrder(int orderId, String customerName, double amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }
}


/*
SINGLE RESPONSIBILITY PRINCIPLE

This class is responsible only for storing an order.
*/
class EcommerceOrderRepository {

    public void saveOrder(EcommerceOrder order) {
        System.out.println("Order " + order.getOrderId()
                + " saved successfully.");
    }
}


/*
SINGLE RESPONSIBILITY PRINCIPLE

This class is responsible only for sending notifications.
*/
class EcommerceNotificationService {

    public void sendEmail(EcommerceOrder order) {
        System.out.println("Email sent to "
                + order.getCustomerName());
    }
}


/*
SINGLE RESPONSIBILITY PRINCIPLE

This class is responsible only for generating invoices.
*/
class EcommerceInvoiceService {

    public void generateInvoice(EcommerceOrder order) {
        System.out.println("Invoice generated for Order "
                + order.getOrderId());
    }
}


/*
SINGLE RESPONSIBILITY PRINCIPLE

This class is responsible only for generating reports.
*/
class EcommerceReportService {

    public void generateReport(EcommerceOrder order) {
        System.out.println("Report generated for Order "
                + order.getOrderId());
    }
}


/*
OPEN/CLOSED PRINCIPLE

The payment system depends on an abstraction.

New payment methods can be added by creating new classes
without modifying the existing payment processor.
*/
interface EcommercePaymentMethod {

    void pay(double amount);
}


/*
OPEN/CLOSED PRINCIPLE

Card payment is one implementation of the payment interface.
*/
class EcommerceCardPayment implements EcommercePaymentMethod {

    @Override
    public void pay(double amount) {
        System.out.println("Payment of " + amount
                + " made using Card.");
    }
}


/*
OPEN/CLOSED PRINCIPLE

UPI payment is another implementation.

The existing Card payment class does not need to be modified.
*/
class EcommerceUpiPayment implements EcommercePaymentMethod {

    @Override
    public void pay(double amount) {
        System.out.println("Payment of " + amount
                + " made using UPI.");
    }
}


/*
OPEN/CLOSED PRINCIPLE

Net Banking is another payment implementation.
*/
class EcommerceNetBankingPayment
        implements EcommercePaymentMethod {

    @Override
    public void pay(double amount) {
        System.out.println("Payment of " + amount
                + " made using Net Banking.");
    }
}


/*
OPEN/CLOSED PRINCIPLE

The payment processor is closed for modification but
open for extension.

New payment types can be added without changing this class.
*/
class EcommercePaymentProcessor {

    public void processPayment(
            EcommercePaymentMethod paymentMethod,
            double amount) {

        paymentMethod.pay(amount);
    }
}


/*
LISKOV SUBSTITUTION PRINCIPLE

Every product must correctly implement the Product behavior.
*/
abstract class EcommerceProduct {

    private String productName;
    private double price;

    EcommerceProduct(String productName, double price) {
        this.productName = productName;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    abstract void deliver();
}


/*
LISKOV SUBSTITUTION PRINCIPLE

A physical product can be substituted wherever
EcommerceProduct is expected.
*/
class EcommercePhysicalProduct extends EcommerceProduct {

    EcommercePhysicalProduct(String productName, double price) {
        super(productName, price);
    }

    @Override
    void deliver() {
        System.out.println("Physical product shipped.");
    }
}


/*
LISKOV SUBSTITUTION PRINCIPLE

A digital product can also be substituted wherever
EcommerceProduct is expected.

It correctly supports the behavior defined by the parent.
*/
class EcommerceDigitalProduct extends EcommerceProduct {

    EcommerceDigitalProduct(String productName, double price) {
        super(productName, price);
    }

    @Override
    void deliver() {
        System.out.println("Digital product delivered by download.");
    }
}


/*
INTERFACE SEGREGATION PRINCIPLE

Instead of one huge interface containing unrelated methods,
the functionality is divided into small focused interfaces.
*/
interface EcommerceShippable {

    void ship();
}


interface EcommerceTrackable {

    void track();
}


interface EcommerceReturnable {

    void returnProduct();
}


/*
INTERFACE SEGREGATION PRINCIPLE

A physical product needs shipping and tracking.
*/
class EcommercePhysicalItem
        implements EcommerceShippable, EcommerceTrackable {

    public void ship() {
        System.out.println("Physical item shipped.");
    }

    public void track() {
        System.out.println("Physical item tracking available.");
    }
}


/*
INTERFACE SEGREGATION PRINCIPLE

A digital product does not need shipping or tracking.

It only implements the functionality that it actually needs.
*/
class EcommerceDigitalItem {

    public void download() {
        System.out.println("Digital item downloaded.");
    }
}


/*
DEPENDENCY INVERSION PRINCIPLE

High-level business logic depends on the abstraction
EcommercePaymentMethod instead of a concrete payment class.
*/
class EcommerceCheckoutService {

    private EcommercePaymentMethod paymentMethod;

    EcommerceCheckoutService(
            EcommercePaymentMethod paymentMethod) {

        this.paymentMethod = paymentMethod;
    }

    public void checkout(EcommerceOrder order) {

        paymentMethod.pay(order.getAmount());

        System.out.println(
                "Checkout completed for Order "
                        + order.getOrderId());
    }
}


/*
MAIN CLASS

Creates objects and demonstrates the SOLID principles.
*/
public class SolidEcommerceDemo {

    public static void main(String[] args) {

        /*
        CREATE ORDER

        EcommerceOrder is responsible only for
        storing order information.
        */
        EcommerceOrder order =
                new EcommerceOrder(
                        101,
                        "Rahul",
                        5000);


        /*
        SINGLE RESPONSIBILITY PRINCIPLE

        Different classes handle different responsibilities.
        */
        EcommerceOrderRepository repository =
                new EcommerceOrderRepository();

        EcommerceNotificationService notification =
                new EcommerceNotificationService();

        EcommerceInvoiceService invoice =
                new EcommerceInvoiceService();

        EcommerceReportService report =
                new EcommerceReportService();

        repository.saveOrder(order);
        notification.sendEmail(order);
        invoice.generateInvoice(order);
        report.generateReport(order);


        /*
        OPEN/CLOSED PRINCIPLE

        Card payment.
        */
        EcommercePaymentProcessor processor =
                new EcommercePaymentProcessor();

        EcommercePaymentMethod cardPayment =
                new EcommerceCardPayment();

        processor.processPayment(
                cardPayment,
                order.getAmount());


        /*
        OPEN/CLOSED PRINCIPLE

        UPI payment can be added without changing
        EcommercePaymentProcessor.
        */
        EcommercePaymentMethod upiPayment =
                new EcommerceUpiPayment();

        processor.processPayment(
                upiPayment,
                order.getAmount());


        /*
        OPEN/CLOSED PRINCIPLE

        Net Banking can also be used without modifying
        the payment processor.
        */
        EcommercePaymentMethod netBanking =
                new EcommerceNetBankingPayment();

        processor.processPayment(
                netBanking,
                order.getAmount());


        /*
        LISKOV SUBSTITUTION PRINCIPLE

        Different product types can be stored using
        the parent class reference.
        */
        EcommerceProduct physicalProduct =
                new EcommercePhysicalProduct(
                        "Laptop",
                        60000);

        EcommerceProduct digitalProduct =
                new EcommerceDigitalProduct(
                        "Java Course",
                        5000);

        physicalProduct.deliver();
        digitalProduct.deliver();


        /*
        INTERFACE SEGREGATION PRINCIPLE

        Physical items implement only the interfaces
        that are relevant to them.
        */
        EcommercePhysicalItem physicalItem =
                new EcommercePhysicalItem();

        physicalItem.ship();
        physicalItem.track();


        /*
        Digital items do not have to implement
        unnecessary shipping functionality.
        */
        EcommerceDigitalItem digitalItem =
                new EcommerceDigitalItem();

        digitalItem.download();


        /*
        DEPENDENCY INVERSION PRINCIPLE

        Checkout depends on EcommercePaymentMethod,
        which is an abstraction.

        The concrete payment method is injected
        into the checkout service.
        */
        EcommercePaymentMethod payment =
                new EcommerceUpiPayment();

        EcommerceCheckoutService checkout =
                new EcommerceCheckoutService(payment);

        checkout.checkout(order);
    }
}