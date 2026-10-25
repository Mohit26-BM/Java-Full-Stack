package com.example.hospital.exception;

/** Category: the requested record does not exist (-> 404). */
public abstract class NotFoundException extends HospitalException {

    private static final long serialVersionUID = 1L;

    protected NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
