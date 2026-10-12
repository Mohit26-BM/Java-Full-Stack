package com.company.jdbc.config;

/*
A snapshot of the pool at one moment.

active  = connections currently borrowed by the application
idle    = connections in the pool, ready to be borrowed
total   = active + idle (never more than maximumPoolSize)
waiting = threads currently waiting for a free connection
*/
public class PoolStats {

    private final int active;
    private final int idle;
    private final int total;
    private final int waiting;
    private final int maximumPoolSize;

    public PoolStats(int active, int idle, int total, int waiting, int maximumPoolSize) {
        this.active = active;
        this.idle = idle;
        this.total = total;
        this.waiting = waiting;
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getActive() {
        return active;
    }

    public int getIdle() {
        return idle;
    }

    public int getTotal() {
        return total;
    }

    public int getWaiting() {
        return waiting;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    @Override
    public String toString() {
        return "active=" + active
                + ", idle=" + idle
                + ", total=" + total + "/" + maximumPoolSize
                + ", waiting threads=" + waiting;
    }
}
