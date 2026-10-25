package com.company.jwt.security;

import io.jsonwebtoken.JwtException;

public class UnknownSigningKeyException extends JwtException {
    public UnknownSigningKeyException(String keyId) {
        super("Unknown signing key: " + keyId);
    }
}
