class ShapeRectangle {

    double length;
    double width;

    ShapeRectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    double calculateArea() {
        return length * width;
    }
}

class RectangleDetails extends ShapeRectangle {

    RectangleDetails(double length, double width) {
        super(length, width);
    }

    double calculatePerimeter() {
        return 2 * (length + width);
    }
}

public class RectangleInheritance {

    public static void main(String[] args) {

        RectangleDetails r = new RectangleDetails(10, 5);

        System.out.println("Area: " + r.calculateArea());
        System.out.println("Perimeter: " + r.calculatePerimeter());
    }
}