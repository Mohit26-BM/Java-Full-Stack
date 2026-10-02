/*
 
                      E-Commerce Platform
                           |
       ┌───────────────────┼────────────────────┐
       ↓                   ↓                    ↓
    Orders              Payments            Products
       |                   |                    |
       ↓                   ↓                    ↓
Repository         PaymentMethod          Product
Notification             |                    |
Invoice          ┌───────┼───────┐      ┌─────┴─────┐
Report            ↓       ↓       ↓      ↓           ↓
                Card     UPI   NetBank  Physical   Digital
  
*/
 
/*
FOOD DELIVERY PLATFORM

This program demonstrates:

1. Identifying classes and objects
2. Encapsulation
3. Inheritance
4. Polymorphism
5. Abstraction
6. Code reuse and reduced duplication
7. Extensibility for future delivery types
8. Dynamic pricing
9. Membership and loyalty benefits
*/

abstract class FoodDeliveryOrder {

    /*
    ENCAPSULATION

    Customer and order information is kept private.
    */
    private int orderId;
    private String customerName;
    private double orderAmount;

    /*
    CONSTRUCTOR

    Initializes common order information.
    */
    FoodDeliveryOrder(int orderId, String customerName,
                      double orderAmount) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.orderAmount = orderAmount;
    }

    /*
    ENCAPSULATION

    Getter methods provide controlled access to private data.
    */
    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    /*
    CODE REUSE

    Common order information is displayed from the parent class.
    */
    public void displayOrderDetails() {

        System.out.println("Order ID: " + orderId);
        System.out.println("Customer Name: " + customerName);
        System.out.println("Order Amount: " + orderAmount);
    }

    /*
    ABSTRACTION

    Every delivery type must calculate its delivery charge,
    but the parent class does not decide how.
    */
    abstract double calculateDeliveryCharge();

    /*
    ABSTRACTION

    Each delivery type provides its own delivery method.
    */
    abstract String getDeliveryType();
}


/*
INHERITANCE

StandardDelivery inherits common properties and methods
from FoodDeliveryOrder.
*/
class StandardFoodDelivery extends FoodDeliveryOrder {

    StandardFoodDelivery(int orderId, String customerName,
                         double orderAmount) {

        super(orderId, customerName, orderAmount);
    }

    /*
    POLYMORPHISM

    Standard delivery has a fixed delivery charge.
    */
    @Override
    double calculateDeliveryCharge() {
        return 50;
    }

    @Override
    String getDeliveryType() {
        return "Standard Delivery";
    }
}


/*
INHERITANCE

DroneDelivery inherits common properties and methods
from FoodDeliveryOrder.
*/
class DroneFoodDelivery extends FoodDeliveryOrder {

    DroneFoodDelivery(int orderId, String customerName,
                      double orderAmount) {

        super(orderId, customerName, orderAmount);
    }

    /*
    POLYMORPHISM

    Drone delivery has a different delivery charge.
    */
    @Override
    double calculateDeliveryCharge() {
        return 100;
    }

    @Override
    String getDeliveryType() {
        return "Drone Delivery";
    }
}


/*
INHERITANCE

PremiumDelivery represents a membership-based delivery
option and inherits common order functionality.
*/
class PremiumFoodDelivery extends FoodDeliveryOrder {

    PremiumFoodDelivery(int orderId, String customerName,
                        double orderAmount) {

        super(orderId, customerName, orderAmount);
    }

    /*
    POLYMORPHISM

    Premium members receive free delivery.
    */
    @Override
    double calculateDeliveryCharge() {
        return 0;
    }

    @Override
    String getDeliveryType() {
        return "Premium Membership Delivery";
    }
}


/*
LOYALTY PROGRAM

This class provides loyalty points based on the
order amount.
*/
class FoodLoyaltyProgram {

    static int calculateLoyaltyPoints(double orderAmount) {
        return (int) orderAmount / 100;
    }
}


/*
DYNAMIC PRICING

This class calculates a dynamic price based on
the current demand level.
*/
class FoodDynamicPricing {

    static double calculateDynamicPrice(double orderAmount,
                                        int demandLevel) {

        if (demandLevel > 80) {
            return orderAmount * 1.20;
        }
        else if (demandLevel > 50) {
            return orderAmount * 1.10;
        }
        else {
            return orderAmount;
        }
    }
}


/*
MAIN CLASS

Creates different delivery objects and demonstrates
polymorphism, loyalty points and dynamic pricing.
*/
public class FoodDeliveryPlatform {

    public static void main(String[] args) {

        /*
        OBJECTS

        Different delivery objects are created using
        the parent class reference.

        This demonstrates POLYMORPHISM.
        */
        FoodDeliveryOrder order1 =
                new StandardFoodDelivery(
                        101, "Rahul", 500);

        FoodDeliveryOrder order2 =
                new DroneFoodDelivery(
                        102, "Amit", 800);

        FoodDeliveryOrder order3 =
                new PremiumFoodDelivery(
                        103, "Priya", 1000);


        /*
        STANDARD DELIVERY
        */
        System.out.println("STANDARD DELIVERY");
        System.out.println();

        order1.displayOrderDetails();

        System.out.println("Delivery Type: " +
                           order1.getDeliveryType());

        System.out.println("Delivery Charge: " +
                           order1.calculateDeliveryCharge());

        System.out.println("Loyalty Points: " +
                           FoodLoyaltyProgram.calculateLoyaltyPoints(
                                   order1.getOrderAmount()));

        System.out.println();


        /*
        DRONE DELIVERY
        */
        System.out.println("DRONE DELIVERY");
        System.out.println();

        order2.displayOrderDetails();

        System.out.println("Delivery Type: " +
                           order2.getDeliveryType());

        System.out.println("Delivery Charge: " +
                           order2.calculateDeliveryCharge());

        System.out.println("Loyalty Points: " +
                           FoodLoyaltyProgram.calculateLoyaltyPoints(
                                   order2.getOrderAmount()));

        System.out.println();


        /*
        PREMIUM MEMBERSHIP DELIVERY
        */
        System.out.println("PREMIUM MEMBERSHIP DELIVERY");
        System.out.println();

        order3.displayOrderDetails();

        System.out.println("Delivery Type: " +
                           order3.getDeliveryType());

        System.out.println("Delivery Charge: " +
                           order3.calculateDeliveryCharge());

        System.out.println("Loyalty Points: " +
                           FoodLoyaltyProgram.calculateLoyaltyPoints(
                                   order3.getOrderAmount()));

        System.out.println();


        /*
        DYNAMIC PRICING

        Demand level of 70 applies a 10% increase.
        */
        double dynamicPrice =
                FoodDynamicPricing.calculateDynamicPrice(1000, 70);

        System.out.println("DYNAMIC PRICING");
        System.out.println("Original Order Amount: 1000");
        System.out.println("Demand Level: 70");
        System.out.println("Dynamic Price: " + dynamicPrice);
    }
}