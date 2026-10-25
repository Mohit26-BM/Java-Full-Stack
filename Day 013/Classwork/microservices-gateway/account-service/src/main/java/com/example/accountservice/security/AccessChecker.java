package com.example.accountservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/** Used from @PreAuthorize: admins see everything, customers only their own records. */
@Component("access")
public class AccessChecker {

    public boolean canAccess(Authentication authentication, Long customerId) {
        if (!(authentication instanceof JwtAuthenticationToken jwt) || customerId == null) {
            return false;
        }
        if ("ADMIN".equals(jwt.getToken().getClaimAsString("role"))) {
            return true;
        }
        Object claim = jwt.getToken().getClaim("customerId");
        return claim instanceof Number n && n.longValue() == customerId;
    }
}
