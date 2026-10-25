package com.company.jwt.security;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.company.jwt.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {
    static final String AUTHORITIES_CLAIM = "authorities";

    private final JwtProperties properties;
    private final JwtKeyRing keyRing;
    private final Clock clock;
    private final JwtParser parser;

    public JwtService(JwtProperties properties, JwtKeyRing keyRing, Clock clock) {
        this.properties = properties;
        this.keyRing = keyRing;
        this.clock = clock;
        this.parser = Jwts.parser()
                .keyLocator(keyRing)
                .requireIssuer(properties.issuer())
                .clock(() -> Date.from(clock.instant()))
                .clockSkewSeconds(properties.clockSkew().toSeconds())
                .build();
    }

    public record IssuedToken(String value, String id, Instant expiresAt) {
    }

    public IssuedToken issueAccessToken(String username, List<String> authorities) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(properties.accessTokenTtl());
        String id = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .header().keyId(keyRing.activeKeyId()).and()
                .issuer(properties.issuer())
                .subject(username)
                .id(id)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(AUTHORITIES_CLAIM, authorities)
                .signWith(keyRing.activeKey(), Jwts.SIG.HS256)
                .compact();
        return new IssuedToken(token, id, expiresAt);
    }

    public Claims verify(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new InvalidBearerTokenException("Token has expired");
        } catch (SignatureException e) {
            throw new InvalidBearerTokenException("Token signature is invalid");
        } catch (UnknownSigningKeyException e) {
            throw new InvalidBearerTokenException("Token was signed with an unknown key");
        } catch (IncorrectClaimException | MissingClaimException e) {
            throw new InvalidBearerTokenException("Token was not issued by this server");
        } catch (UnsupportedJwtException e) {
            throw new InvalidBearerTokenException("Token must be signed");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidBearerTokenException("Token is malformed");
        } catch (JwtException e) {
            throw new InvalidBearerTokenException("Token is invalid");
        }
    }

    @SuppressWarnings("unchecked")
    static List<String> authoritiesOf(Claims claims) {
        Object value = claims.get(AUTHORITIES_CLAIM);
        return value instanceof List<?> list ? (List<String>) list : List.of();
    }
}
