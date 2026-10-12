package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.model.BenchmarkResult;

public interface BenchmarkService {

    /*
    Inserts rowCount generated rows into expenses_benchmark with
    each strategy and returns the timings.
    */
    List<BenchmarkResult> run(int rowCount);
}
