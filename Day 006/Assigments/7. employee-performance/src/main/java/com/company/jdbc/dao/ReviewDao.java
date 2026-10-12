package com.company.jdbc.dao;

import com.company.jdbc.model.PerformanceReview;

public interface ReviewDao {

    /*
    Returns the generated review_id.
    */
    long add(PerformanceReview review);
}
