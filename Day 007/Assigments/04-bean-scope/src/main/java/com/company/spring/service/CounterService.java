package com.company.spring.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.DisposableBean;

/*
The SAME class is used for every scope; only the configuration
changes (see SingletonConfig and PrototypeConfig).

- Each object gets a sequence number (#1, #2, ...) so it is easy
  to see how many objects Spring created.
- count is per-object state: if two references share one object,
  incrementing through one is visible through the other.
- DisposableBean.destroy() is Spring's "about to be destroyed"
  callback - it shows which scopes Spring cleans up on close.
*/
public class CounterService implements DisposableBean {

    private static final AtomicInteger INSTANCES_CREATED = new AtomicInteger();

    private final int instanceNumber;
    private int count;

    public CounterService() {

        instanceNumber = INSTANCES_CREATED.incrementAndGet();

        System.out.println("   [CounterService #" + instanceNumber + "] constructor called");
    }

    public int increment() {
        return ++count;
    }

    public int getCount() {
        return count;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public static int getInstancesCreated() {
        return INSTANCES_CREATED.get();
    }

    @Override
    public void destroy() {
        System.out.println("   [CounterService #" + instanceNumber + "] destroy() called by Spring");
    }

    @Override
    public String toString() {
        return "CounterService #" + instanceNumber + " (count=" + count + ")";
    }
}
