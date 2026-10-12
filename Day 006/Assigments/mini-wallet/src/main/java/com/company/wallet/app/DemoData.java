package com.company.wallet.app;

import java.util.concurrent.atomic.AtomicLong;

/*
Unique email addresses and phone numbers for demo customers,
so the demonstrations can be run again and again without
hitting the UNIQUE constraints by accident.
*/
final class DemoData {

    private static final AtomicLong COUNTER =
            new AtomicLong(System.currentTimeMillis() % 1_000_000_000L);

    private DemoData() {
    }

    static long next() {
        return COUNTER.incrementAndGet() % 1_000_000_000L;
    }

    static String email(String prefix, long id) {
        return prefix + "." + id + "@demo.example.com";
    }

    /*
    10 digits starting with 7, e.g. 7123456789.
    */
    static String phone(long id) {
        return String.format("7%09d", id);
    }
}
