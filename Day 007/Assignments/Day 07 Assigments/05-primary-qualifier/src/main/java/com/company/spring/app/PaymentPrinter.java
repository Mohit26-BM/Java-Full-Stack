package com.company.spring.app;

import java.util.List;

import com.company.spring.payment.PaymentReceipt;
import com.company.spring.repository.PaymentRepository;

/*
Prints the latest rows of a05_payments, to show which implementation
actually handled each payment.
*/
final class PaymentPrinter {

    private PaymentPrinter() {
    }

    static void printReceipt(PaymentReceipt receipt) {
        System.out.println("   Saved to a05_payments: " + receipt);
    }

    static void printRecent(PaymentRepository repository, int limit) {

        List<PaymentReceipt> receipts = repository.findRecent(limit);

        System.out.println("\n   Latest rows in a05_payments:");
        System.out.println("   ---------------------------------------------------------");
        System.out.println(String.format("   %-5s %-9s %12s  %-7s %s", "ID", "Order", "Amount", "Method", "Reference"));
        System.out.println("   ---------------------------------------------------------");

        for (PaymentReceipt receipt : receipts) {
            System.out.println("   " + receipt);
        }
    }
}
