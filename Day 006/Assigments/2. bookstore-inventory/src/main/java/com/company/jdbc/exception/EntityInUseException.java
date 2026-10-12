package com.company.jdbc.exception;

/*
Thrown when an author or category cannot be deleted because books still refer to it.
*/
public class EntityInUseException extends Exception {

    private static final long serialVersionUID = 1L;

    public EntityInUseException(String message) {
        super(message);
    }
}