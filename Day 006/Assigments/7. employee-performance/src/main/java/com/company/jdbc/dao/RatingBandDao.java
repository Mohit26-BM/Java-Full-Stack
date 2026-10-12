package com.company.jdbc.dao;

import java.math.BigDecimal;
import java.util.List;

import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.RatingBand;

public interface RatingBandDao {

    /*
    Highest threshold first.
    */
    List<RatingBand> findAll();

    /*
    Returns false when the band does not exist.
    Throws InvalidInputException when another band already
    uses the same threshold (UNIQUE constraint).
    */
    boolean updateMinRating(String bandName, BigDecimal minRating)
            throws InvalidInputException;
}
