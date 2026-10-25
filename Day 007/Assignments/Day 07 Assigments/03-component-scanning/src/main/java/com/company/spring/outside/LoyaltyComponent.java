package com.company.spring.outside;

import org.springframework.stereotype.Component;

/*
OPTIONAL EXTENSION: a class OUTSIDE the scanned package.

It is annotated with @Component, but AppConfig only scans
com.company.spring.customer - so Spring never sees this class
and never creates it. The annotation alone does nothing.
*/
@Component
public class LoyaltyComponent {

    public LoyaltyComponent() {
        System.out.println("   [@Component  LoyaltyComponent]   created");
    }

    public int pointsFor(double amountSpent) {
        return (int) (amountSpent / 100);
    }
}
