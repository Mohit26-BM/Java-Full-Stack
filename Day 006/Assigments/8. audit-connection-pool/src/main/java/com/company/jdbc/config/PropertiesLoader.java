package com.company.jdbc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
Reads a .properties file from src/main/resources (the classpath).
*/
public final class PropertiesLoader {

    private PropertiesLoader() {
    }

    public static Properties load(String fileName) {

        Properties properties = new Properties();

        try (InputStream input =
                     PropertiesLoader.class
                             .getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new IllegalStateException(
                        fileName + " was not found on the classpath."
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new IllegalStateException("Unable to read " + fileName, e);
        }

        return properties;
    }

    /*
    Reads a required number from the properties.
    */
    static long requireLong(Properties properties, String key) {

        String value = properties.getProperty(key);

        if (value == null) {
            throw new IllegalStateException("Missing setting: " + key);
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Setting " + key + " must be a number, found: " + value);
        }
    }
}
