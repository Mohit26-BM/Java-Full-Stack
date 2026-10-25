package com.company.spring.lifecycle;

/*
Numbers every lifecycle message (STEP 1, STEP 2 ...) so the
order in which Spring calls things is obvious in the console.
*/
public final class LifecycleLog {

    private static int step;

    private LifecycleLog() {
    }

    public static synchronized void step(String who, String what) {
        step++;
        System.out.println(String.format("   STEP %-2d | %-20s | %s", step, who, what));
    }

    public static synchronized void reset() {
        step = 0;
    }
}
