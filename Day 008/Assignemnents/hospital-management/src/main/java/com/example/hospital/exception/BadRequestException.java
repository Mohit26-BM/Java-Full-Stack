package com.example.hospital.exception;

/** Category: the input values themselves are invalid (-> 400). */
public abstract class BadRequestException extends HospitalException {

    private static final long serialVersionUID = 1L;

    protected BadRequestException(String errorCode, String message) {
        super(errorCode, message);
    }
}
