package com.company.jdbc.model;

import java.math.BigDecimal;

/*
One row of rating_bands: an average rating >= minRating belongs to
this band, unless a band with a higher minRating also matches.
*/
public class RatingBand {

    private final String bandName;
    private final BigDecimal minRating;

    public RatingBand(String bandName, BigDecimal minRating) {
        this.bandName = bandName;
        this.minRating = minRating;
    }

    public String getBandName() {
        return bandName;
    }

    public BigDecimal getMinRating() {
        return minRating;
    }
}
