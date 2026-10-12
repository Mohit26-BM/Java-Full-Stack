package com.company.jdbc.config;

import java.util.Properties;

/*
Application settings from app.properties.
*/
public final class AppConfig {

    private static final Properties PROPERTIES =
            PropertiesLoader.load("app.properties");

    private static final int FALLBACK_THRESHOLD = 5;

    private AppConfig() {
    }

    /*
    Configurable threshold for the low-stock report.
    Falls back to 5 if the value is missing or not a positive number.
    */
    public static int getLowStockThreshold() {

        String value =
                PROPERTIES.getProperty("inventory.lowStockThreshold");

        try {

            int threshold = Integer.parseInt(value.trim());

            return threshold > 0 ? threshold : FALLBACK_THRESHOLD;

        } catch (NumberFormatException | NullPointerException e) {

            return FALLBACK_THRESHOLD;
        }
    }
}
