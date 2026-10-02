import java.util.function.Function;

public class ProductPriceTransformation {

    public static void main(String[] args) {

        ProductData product =
                new ProductData(101, "Laptop", "Electronics", 80000);

        Function<ProductData, String> productName =
                p -> p.name;

        Function<ProductData, Double> productPrice =
                p -> p.price;

        Function<ProductData, Double> discountedPrice =
                p -> p.price - (p.price * 0.10);

        Function<ProductData, String> productCategory =
                p -> p.category;

        System.out.println("Product ID: " + product.id);

        System.out.println("Product Name: "
                + productName.apply(product));

        System.out.println("Category: "
                + productCategory.apply(product));

        System.out.println("Price: "
                + productPrice.apply(product));

        System.out.println("Discounted Price: "
                + discountedPrice.apply(product));
    }
}