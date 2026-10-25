package com.example.product.exception;

/** Optimistic locking: the client sent an old version (-> 409 Conflict). */
public class ProductModifiedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductModifiedException(Long id, Long clientVersion, Long currentVersion) {
        super("Product " + id + " was changed by someone else (you sent version " + clientVersion
                + ", current version is " + currentVersion + "). Reload it and try again.");
    }
}
