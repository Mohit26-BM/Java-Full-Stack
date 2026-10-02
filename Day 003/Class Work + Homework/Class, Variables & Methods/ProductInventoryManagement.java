class InventoryProduct {

    int productId;
    String productName;
    double price;
    int quantity;

    static String companyName = "ABC Store";
    static int productCount = 0;

    InventoryProduct(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;

        productCount++;
    }

    void displayProduct() {
        System.out.println("Product ID: " + productId);
        System.out.println("Product Name: " + productName);
        System.out.println("Price: " + price);
        System.out.println("Quantity: " + quantity);
        System.out.println("Total Value: " + calculateTotalValue());
        System.out.println();
    }

    double calculateTotalValue() {
        return price * quantity;
    }

    void addStock(int quantity) {
        this.quantity = this.quantity + quantity;
        System.out.println(quantity + " items added to stock.");
    }

    void removeStock(int quantity) {

        if (quantity <= this.quantity) {
            this.quantity = this.quantity - quantity;
            System.out.println(quantity + " items removed from stock.");
        }
        else {
            System.out.println("Insufficient stock. Stock cannot become negative.");
        }
    }

    static void displayCompanyName() {
        System.out.println("Company Name: " + companyName);
    }

    static void displayProductCount() {
        System.out.println("Total Products Created: " + productCount);
    }
}

public class ProductInventoryManagement {

    public static void main(String[] args) {

        InventoryProduct.displayCompanyName();

        InventoryProduct p1 =
                new InventoryProduct(101, "Laptop", 50000, 10);

        InventoryProduct p2 =
                new InventoryProduct(102, "Mouse", 1000, 20);

        InventoryProduct p3 =
                new InventoryProduct(103, "Keyboard", 2000, 15);

        System.out.println("\nProduct 1 Details:");
        p1.displayProduct();

        System.out.println("Product 2 Details:");
        p2.displayProduct();

        System.out.println("Product 3 Details:");
        p3.displayProduct();

        System.out.println("Adding stock to Product 1:");
        p1.addStock(5);

        System.out.println("\nRemoving stock from Product 2:");
        p2.removeStock(5);

        System.out.println("\nTrying to remove excess stock from Product 3:");
        p3.removeStock(20);

        System.out.println("\nUpdated Product 1 Details:");
        p1.displayProduct();

        System.out.println("Updated Product 2 Details:");
        p2.displayProduct();

        System.out.println("Updated Product 3 Details:");
        p3.displayProduct();

        InventoryProduct.displayProductCount();
    }
}