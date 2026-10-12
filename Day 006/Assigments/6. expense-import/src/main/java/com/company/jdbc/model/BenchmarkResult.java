package com.company.jdbc.model;

/*
Timing of one insert strategy in the benchmark.
*/
public class BenchmarkResult {

    private final String strategy;
    private final int rowsInserted;
    private final long elapsedMillis;

    public BenchmarkResult(String strategy, int rowsInserted, long elapsedMillis) {
        this.strategy = strategy;
        this.rowsInserted = rowsInserted;
        this.elapsedMillis = elapsedMillis;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getRowsInserted() {
        return rowsInserted;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public long getRowsPerSecond() {
        return elapsedMillis == 0 ? rowsInserted * 1000L : rowsInserted * 1000L / elapsedMillis;
    }
}
