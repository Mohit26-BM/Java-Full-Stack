package com.company.jwt.security;

import java.security.Key;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.company.jwt.config.JwtProperties;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtKeyRing extends LocatorAdapter<Key> {
    private final String activeKeyId;
    private final Map<String, SecretKey> keys;

    public JwtKeyRing(JwtProperties properties) {
        this.keys = properties.keys().entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(entry.getValue()))));
        if (!keys.containsKey(properties.activeKeyId())) {
            throw new IllegalStateException("portal.jwt.active-key-id '" + properties.activeKeyId()
                    + "' is not one of the configured keys " + keys.keySet());
        }
        this.activeKeyId = properties.activeKeyId();
    }

    public String activeKeyId() {
        return activeKeyId;
    }

    public SecretKey activeKey() {
        return keys.get(activeKeyId);
    }

    @Override
    protected Key locate(JwsHeader header) {
        String keyId = header.getKeyId();
        SecretKey key = keyId == null ? null : keys.get(keyId);
        if (key == null) {
            throw new UnknownSigningKeyException(keyId);
        }
        return key;
    }
}
