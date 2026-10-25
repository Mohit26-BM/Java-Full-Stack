package com.company.spring.service;

/*
Optional extension: a second, INDEPENDENT bean.
It has nothing to do with ProductService; both are registered
in the same container and retrieved separately.
*/
public class DiscountService {

    private final int discountPercent;

    public DiscountService(int discountPercent) {

        this.discountPercent = discountPercent;

        System.out.println("   [DiscountService] constructor called - object created "
                + "(identity " + Integer.toHexString(System.identityHashCode(this)) + ")");
    }

    public double applyDiscount(double price) {
        return price - (price * discountPercent / 100.0);
    }

    public void displayOffer() {
        System.out.println("Festival offer: " + discountPercent + "% off on all products");
    }
}
