package com.example.hospital.exception;

/** Category: the request clashes with existing data (duplicate, overlap, stale version) (-> 409). */
public abstract class ConflictException extends HospitalException {

    private static final long serialVersionUID = 1L;

    protected ConflictException(String errorCode, String message) {
        super(errorCode, message);
    }
}
