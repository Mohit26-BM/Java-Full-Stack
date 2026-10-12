# Spring Security and JWT: Detailed Notes

Authentication vs authorization, how Spring Security works internally (filter chain and authentication flow), password hashing, session vs token authentication, JWT in depth, a complete step-by-step JWT implementation for a REST API, role and method security, CORS and CSRF, refresh tokens, React integration, OAuth2 overview, testing, attacks and defenses, debugging 401 and 403, and interview questions.

> Targets **Spring Boot 3 / Spring Security 6** (`jakarta.*`, `SecurityFilterChain` beans). Section 1.4 lists outdated patterns that older tutorials still use. The API examples build on the REST API notes (DTOs, global exception handler, CORS).

---

## Table of Contents

1. [Security Fundamentals](#1-security-fundamentals)
2. [How Spring Security Works](#2-how-spring-security-works)
3. [Password Storage](#3-password-storage)
4. [Session-Based vs Token-Based Authentication](#4-session-based-vs-token-based-authentication)
5. [Basic Spring Security Configuration](#5-basic-spring-security-configuration)
6. [JWT in Depth](#6-jwt-in-depth)
7. [Implementing JWT Authentication, Step by Step](#7-implementing-jwt-authentication-step-by-step)
8. [Authorization in Practice](#8-authorization-in-practice)
9. [CORS and CSRF](#9-cors-and-csrf)
10. [Refresh Tokens, Logout, Revocation](#10-refresh-tokens-logout-revocation)
11. [The Client Side (React)](#11-the-client-side-react)
12. [OAuth2, OpenID Connect and Microservices](#12-oauth2-openid-connect-and-microservices)
13. [Testing Secured Endpoints](#13-testing-secured-endpoints)
14. [Common Attacks and Defenses](#14-common-attacks-and-defenses)
15. [Debugging 401 and 403](#15-debugging-401-and-403)
16. [Common Mistakes](#16-common-mistakes)
17. [Interview Questions](#17-interview-questions)
18. [Cheat Sheet](#18-cheat-sheet)
19. [Practice Tasks with Solutions](#19-practice-tasks-with-solutions)

---

## 1. Security Fundamentals

### 1.1 Authentication vs authorization

| | Authentication (**AuthN**) | Authorization (**AuthZ**) |
|---|---|---|
| Question | **Who are you?** | **What are you allowed to do?** |
| Proof | Password, token, fingerprint | Roles, permissions, ownership |
| Happens | First | After authentication |
| Failure status | **401 Unauthorized** | **403 Forbidden** |
| Airport analogy | **Passport check** at the gate | **Boarding pass**: which flight, which lounge |

### 1.2 Core vocabulary

| Term | Meaning |
|---|---|
| **Principal** | The authenticated user (or system) |
| **Credentials** | What proves identity (password, token) |
| **Authority** | A permission string, such as `ROLE_ADMIN` or `READ_REPORTS` |
| **Role** | A coarse group of authorities. In Spring, a role is an authority **prefixed with `ROLE_`** |
| **SecurityContext** | Holds the current `Authentication` for the running request |
| **Authentication** | The object representing who is logged in (principal, credentials, authorities) |

### 1.3 What security must cover

Confidentiality (HTTPS, hashing), integrity (signatures, validation), authentication, authorization, and protection against common attacks (section 14).

### 1.4 Outdated patterns you will meet in older tutorials

| Old (do not use) | Modern (Spring Security 6) |
|---|---|
| `extends WebSecurityConfigurerAdapter` (removed) | A `@Bean SecurityFilterChain` |
| `http.authorizeRequests()` | `http.authorizeHttpRequests(...)` |
| `antMatchers(...)`, `mvcMatchers(...)` | `requestMatchers(...)` |
| `.and()` chaining | Lambda DSL: `.csrf(c -> c.disable())` |
| `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| `javax.servlet.*` | `jakarta.servlet.*` |
| JJWT `setSubject`, `parser().setSigningKey(...)`, `SignatureAlgorithm` | JJWT 0.12: `subject(...)`, `parser().verifyWith(key)`, `parseSignedClaims` |
| Configuring `AuthenticationManagerBuilder` in a config adapter | Expose `AuthenticationManager` from `AuthenticationConfiguration` |

---

## 2. How Spring Security Works

### 2.1 What happens when you add the starter

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

With **no other code**, Spring Boot:

- secures **every endpoint**,
- creates a user named **`user`** with a random password printed in the console (`Using generated security password: ...`),
- enables a **form login page** and **HTTP Basic**,
- enables **CSRF protection** and adds security headers.

Opening any URL redirects to `/login`.

### 2.2 It is a chain of servlet filters

All of Spring Security works as **servlet filters** that run **before** your controllers.

```
HTTP request
   |
   v
DelegatingFilterProxy -> FilterChainProxy -> SecurityFilterChain
                                                 |
        [CorsFilter] -> [CsrfFilter] -> [LogoutFilter]
          -> [UsernamePasswordAuthenticationFilter]   <- our JWT filter goes BEFORE this
          -> [BasicAuthenticationFilter]
          -> [SecurityContextHolderFilter]
          -> [ExceptionTranslationFilter]    (turns security exceptions into 401/403)
          -> [AuthorizationFilter]           (checks the access rules)
                                                 |
                                                 v
                                        DispatcherServlet -> Controller
```

**Analogy:** airport security. Passengers (requests) pass **several checkpoints in sequence**: ID check, baggage scan, boarding-pass gate. Fail any, and you are stopped before reaching the plane (the controller).

| Filter | Job |
|---|---|
| `CorsFilter` | Handles CORS preflight |
| `CsrfFilter` | Verifies the CSRF token on state-changing requests |
| `UsernamePasswordAuthenticationFilter` | Processes form login |
| `BasicAuthenticationFilter` | Processes HTTP Basic credentials |
| `ExceptionTranslationFilter` | Converts `AuthenticationException` to 401 and `AccessDeniedException` to 403 |
| `AuthorizationFilter` | Applies `authorizeHttpRequests` rules |

### 2.3 The authentication flow

```
Credentials (username + password)
        |
        v
AuthenticationManager                    (interface; usually ProviderManager)
        |  tries its providers
        v
AuthenticationProvider                   (usually DaoAuthenticationProvider)
        |        \
        |         \--> PasswordEncoder.matches(raw, stored hash)
        v
UserDetailsService.loadUserByUsername()  -> UserDetails (from DB)
        |
        v
Authentication (authenticated=true, with authorities)
        |
        v
SecurityContextHolder.getContext().setAuthentication(...)
```

### 2.4 Core components

| Component | Role |
|---|---|
| `SecurityFilterChain` | The configured chain: rules, filters, session policy |
| `AuthenticationManager` | Entry point to authenticate credentials |
| `AuthenticationProvider` | Does the actual verification (`DaoAuthenticationProvider` uses a database) |
| `UserDetailsService` | **Loads a user by username** (you implement it) |
| `UserDetails` | Represents a user: username, password hash, authorities, flags |
| `PasswordEncoder` | Hashes and verifies passwords |
| `Authentication` | The authenticated identity |
| `SecurityContextHolder` | Gives access to the current `SecurityContext` (stored per thread, `ThreadLocal`) |
| `GrantedAuthority` | One permission (`SimpleGrantedAuthority("ROLE_ADMIN")`) |

### 2.5 Reading the current user

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();

// in a controller, easier:
@GetMapping("/me")
public String me(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
}
```

---

## 3. Password Storage

### Never store plain text, and never use MD5 or SHA-1/SHA-256 alone

| Technique | Reversible? | Use for |
|---|---|---|
| **Encoding** (Base64) | Yes, trivially | Data format only, **not security** |
| **Encryption** (AES) | Yes, with a key | Data you must read back (card tokens, files) |
| **Hashing** (BCrypt) | **No**, one-way | **Passwords** |

**Analogy:** encryption is a **locked box** (whoever has the key opens it). Hashing is a **paper shredder**: you can check a document is the same by shredding a copy and comparing the pieces, but you cannot reassemble the original.

### Why BCrypt

- **Salted**: a random salt is stored inside the hash, so identical passwords produce different hashes (defeats rainbow tables).
- **Adaptive/slow by design**: the cost factor (default 10) makes brute-force guessing expensive; you can raise it as hardware improves.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();            // strength 10 by default
}

String hash = passwordEncoder.encode("secret123");        // $2a$10$N9qo8uLOickgx2ZMRZoMye...
passwordEncoder.matches("secret123", hash);                // true
```

- You **cannot decrypt** a hash. Login works by hashing the entered password and comparing (`matches`).
- Every `encode` call returns a **different** string for the same password (different salt); always verify with `matches`, never `equals`.
- Spring's `PasswordEncoderFactories.createDelegatingPasswordEncoder()` stores a prefix (`{bcrypt}...`) so you can migrate algorithms later.
- Other modern choices: **Argon2**, **scrypt**, **PBKDF2**.

### Password policy

Enforce a minimum length (8+; longer is better), reject common passwords, never email a password, and rate-limit login attempts.

---

## 4. Session-Based vs Token-Based Authentication

### Session (stateful)

1. User logs in; the server creates a **session** (stored in memory or a shared store) and sends back a **session ID cookie** (`JSESSIONID`).
2. The browser sends the cookie automatically on each request.
3. The server looks up the session to know who you are.

**Analogy:** a **hotel keycard** registered at the front desk. The system keeps a record of which cards are valid.

### Token (stateless)

1. User logs in; the server returns a **signed token** (JWT).
2. The client sends it in `Authorization: Bearer <token>` with every request.
3. The server **verifies the signature** and reads the claims. **No lookup needed.**

**Analogy:** a **stamped, signed concert wristband**. The gate staff verify the stamp themselves without calling the box office.

### Comparison

| | Session + cookie | JWT (token) |
|---|---|---|
| Server state | **Stored** per user | **None** (stateless) |
| Scaling | Needs sticky sessions or a shared store (for example Redis) | Any instance can verify |
| Revocation / logout | **Easy**: delete the session | **Hard**: valid until it expires |
| Sent via | Cookie (automatic) | Header (you attach it) |
| CSRF risk | **Yes** (cookies are automatic) | Not when sent in a header |
| XSS risk | Mitigated by `HttpOnly` cookies | High if stored in `localStorage` |
| Best for | Server-rendered sites, same-origin apps | SPAs, mobile apps, APIs, microservices |

> **A candid view:** JWT is **not automatically better**. For a single web app with its own backend, plain server sessions with `HttpOnly` cookies are simpler and easier to revoke. JWT shines when you have a SPA or mobile client, multiple services, or a separate identity provider. Choose by need, not by trend.

---

## 5. Basic Spring Security Configuration

Before JWT, learn the core configuration.

### The `SecurityFilterChain` bean

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

### Rule methods

| Method | Meaning |
|---|---|
| `permitAll()` | Anyone, including anonymous users |
| `denyAll()` | Nobody |
| `authenticated()` | Any logged-in user |
| `hasRole("ADMIN")` | Has authority **`ROLE_ADMIN`** |
| `hasAnyRole("ADMIN", "MANAGER")` | Any of these roles |
| `hasAuthority("REPORT_READ")` | Has this exact authority |
| `access(...)` | Custom expression or manager |

**Rules are evaluated top to bottom; the first match wins.** Put specific rules before general ones, and `anyRequest()` last.

```java
.requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()   // reading is public
.requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
.anyRequest().authenticated()
```

### Role vs authority: the `ROLE_` prefix

- `hasRole("ADMIN")` checks for the authority **`ROLE_ADMIN`**.
- `User.withUsername(...).roles("ADMIN")` stores `ROLE_ADMIN` automatically.
- `.authorities("ROLE_ADMIN")` or `new SimpleGrantedAuthority("ROLE_ADMIN")` stores exactly what you give.

A mismatch here is the most common cause of unexpected `403` responses.

### Quick in-memory users (demos only)

```java
@Bean
public UserDetailsService users(PasswordEncoder encoder) {
    UserDetails user = User.withUsername("asha")
            .password(encoder.encode("pass123")).roles("USER").build();
    UserDetails admin = User.withUsername("admin")
            .password(encoder.encode("admin123")).roles("ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
}
```

### Method-level security

```java
@Configuration
@EnableMethodSecurity            // turns on @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig { }

@Service
public class ReportService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAll() { ... }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Profile getProfile(String username) { ... }
}
```

---

## 6. JWT in Depth

### 6.1 What is a JWT

> A **JSON Web Token** is a compact, URL-safe token carrying **claims** (data) that is **digitally signed**, so the receiver can verify it was issued by a trusted party and not altered.

**Analogy:** a **signed and stamped ID card with an expiry date**. Anyone can **read** what is printed on it, but only the issuer's stamp makes it valid, and any alteration breaks the stamp.

### 6.2 Structure: three Base64URL parts separated by dots

```
xxxxx.yyyyy.zzzzz
header . payload . signature
```

```
Header    {"alg":"HS256","typ":"JWT"}

Payload   {"sub":"asha","roles":["ROLE_USER"],"iat":1760270000,"exp":1760270900}

Signature HMAC-SHA256( base64url(header) + "." + base64url(payload), secretKey )
```

| Part | Contains |
|---|---|
| **Header** | Token type and the signing algorithm |
| **Payload** | The **claims** |
| **Signature** | Proof of integrity and origin |

### 6.3 Standard claims

| Claim | Meaning |
|---|---|
| `sub` | Subject (who the token is about, usually the username or user id) |
| `iss` | Issuer |
| `aud` | Audience (who it is for) |
| `exp` | **Expiry** time |
| `iat` | Issued-at time |
| `nbf` | Not valid before |
| `jti` | Unique token ID |
| *custom* | `roles`, `tenantId`, and so on |

### 6.4 Signed is not encrypted

**The payload is only Base64-encoded, not secret.** Anyone who holds the token can decode it (try jwt.io). The signature guarantees **integrity**, not **confidentiality**.

> **Never put passwords, card numbers or other secrets in a JWT.** Keep claims minimal.

### 6.5 How verification works

1. The server splits the token into three parts.
2. It recomputes the signature from `header.payload` with its key.
3. If it matches the token's signature, the contents are untampered. The server then checks `exp` (and `iss`, `aud`).
4. No database is needed to trust the token, which is what makes it stateless.

### 6.6 Signing algorithms

| | HS256 (HMAC) | RS256 (RSA) |
|---|---|---|
| Key type | **One shared secret** (symmetric) | **Private key signs, public key verifies** (asymmetric) |
| Who can create tokens | Anyone with the secret | Only the holder of the private key |
| Who can verify | Anyone with the secret (so they could also forge) | Anyone with the **public** key |
| Use when | One application issues and verifies | **Several services verify** tokens from a central issuer (microservices, OAuth2 servers) |

For HS256, the secret must be **at least 256 bits (32 bytes) of randomness**.

### 6.7 Access token vs refresh token

| | Access token | Refresh token |
|---|---|---|
| Purpose | Call the API | Obtain **new access tokens** |
| Lifetime | **Short** (5 to 30 minutes) | Long (days or weeks) |
| Sent to | Every API request | Only the auth endpoint |
| Stored on server | No | **Yes** (so it can be revoked) |

**Analogy:** an access token is a **day pass**; a refresh token is the **membership card** you show to get a new day pass. Short-lived passes limit damage if one is stolen.

### 6.8 JWT weaknesses and cautions

| Issue | Defense |
|---|---|
| **Cannot be revoked** before expiry | Short expiry, refresh-token rotation, optional deny-list |
| **Payload is readable** | Store nothing sensitive |
| **Stolen token = full access** until it expires | HTTPS, short life, careful client storage |
| **Weak secret** can be brute-forced offline | Long random key from an environment variable or secrets manager |
| **`alg: none` attack** (unsigned token accepted) | Use a library that **requires** a signature and a pinned key |
| **Algorithm confusion** (RS256 vs HS256) | Pin the expected algorithm and key type |
| **Not validating `exp`, `iss`, `aud`** | Validate them (the library does `exp` automatically) |
| **Token bloat** | Keep claims small; headers are size-limited |

---

## 7. Implementing JWT Authentication, Step by Step

A stateless REST API with **register, login, protected endpoints and roles**.

### 7.1 The request flow

```
1. POST /api/auth/register   {username, password}      -> password hashed with BCrypt, user saved
2. POST /api/auth/login      {username, password}      -> AuthenticationManager verifies
                                                          -> JwtService creates a token -> {"token": "..."}
3. GET  /api/v1/employees    Authorization: Bearer <token>
        |
        v
   JwtAuthenticationFilter:  extract token -> verify signature/expiry -> load user
                             -> put Authentication in SecurityContext
        |
        v
   AuthorizationFilter: rule check (authenticated? role?)  ->  controller  ->  JSON
```

### 7.2 Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.x</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.x</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.x</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

(Use the latest 0.12.x release of JJWT.)

### 7.3 Configuration

```properties
# a Base64-encoded random key of at least 32 bytes, supplied from an ENVIRONMENT VARIABLE
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=900000
```

Generate a key: `openssl rand -base64 32`. **Never commit the secret to Git.**

### 7.4 The user entity and repository

Name the entity `AppUser` to avoid clashing with Spring's `User` class.

```java
public enum Role { USER, ADMIN }

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;                 // BCrypt hash, never plain text

    @Enumerated(EnumType.STRING)
    private Role role;

    protected AppUser() { }
    public AppUser(String username, String password, Role role) {
        this.username = username; this.password = password; this.role = role;
    }
    // getters
}

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

### 7.5 `UserDetailsService`: how Spring loads a user

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities("ROLE_" + u.getRole().name())     // ROLE_ prefix for hasRole(...)
                .build();
    }
}
```

### 7.6 `JwtService`: create and verify tokens (JJWT 0.12)

```java
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)                        // HS256 chosen from the key size
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            return parseClaims(token).getSubject().equals(user.getUsername());
        } catch (JwtException | IllegalArgumentException e) {
            return false;                             // bad signature, expired, malformed...
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                   .verifyWith(key)                   // checks the signature
                   .build()
                   .parseSignedClaims(token)          // also checks exp; rejects unsigned tokens
                   .getPayload();
    }
}
```

`parseSignedClaims` throws `ExpiredJwtException`, `SignatureException`, `MalformedJwtException` (all `JwtException`s) for bad tokens.

### 7.7 The JWT filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);               // no token: continue anonymously
            return;
        }

        String token = header.substring(7);
        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (jwtService.isValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // invalid token: leave the context empty so the entry point returns 401
        }

        chain.doFilter(request, response);
    }
}
```

- `OncePerRequestFilter` guarantees it runs **once** per request.
- The filter **never rejects directly**; it only **establishes identity** if the token is valid. The authorization rules decide access afterward.
- Loading the user from the database each request keeps roles current and lets you disable a user instantly, at the cost of a lookup. To avoid the lookup, build authorities from the token's `roles` claim instead (faster, but changes in roles apply only after the token expires).

### 7.8 JSON responses for 401 and 403

Exceptions thrown in **security filters never reach your `@RestControllerAdvice`**. Provide handlers so API clients get JSON instead of HTML or an empty body.

```java
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    public RestAuthenticationEntryPoint(ObjectMapper mapper) { this.mapper = mapper; }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         AuthenticationException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);                 // 401
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(),
            Map.of("status", 401, "message", "Authentication required or token invalid"));
    }
}

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;
    public RestAccessDeniedHandler(ObjectMapper mapper) { this.mapper = mapper; }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);                    // 403
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(),
            Map.of("status", 403, "message", "You do not have permission to do this"));
    }
}
```

> Without an entry point and with `formLogin`/`httpBasic` not configured, Spring Boot's default for an anonymous request is **403**, not 401. Configure the entry point to get the correct 401.

### 7.9 The security configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final RestAuthenticationEntryPoint entryPoint;
    private final RestAccessDeniedHandler deniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          RestAuthenticationEntryPoint entryPoint,
                          RestAccessDeniedHandler deniedHandler) {
        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                        // stateless API, no cookie auth
            .cors(Customizer.withDefaults())                     // uses the CorsConfigurationSource bean
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)            // 401 as JSON
                .accessDeniedHandler(deniedHandler))             // 403 as JSON
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();     // wires your UserDetailsService + PasswordEncoder
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
```

Why each line:

| Setting | Reason |
|---|---|
| `csrf.disable()` | Token in a header is not sent automatically by the browser, so CSRF does not apply. (Keep CSRF **on** for cookie-based apps.) |
| `STATELESS` | Do not create or use an `HttpSession`; every request must carry the token |
| `permitAll` on `/api/auth/**` | Login and register must be reachable without a token |
| `addFilterBefore(jwtFilter, ...)` | The JWT must be processed before Spring tries other authentication |
| `authenticationEntryPoint` | Returns **401 JSON** for missing or invalid tokens |
| `accessDeniedHandler` | Returns **403 JSON** for insufficient roles |
| `cors(...)` in the chain | Preflight `OPTIONS` requests must be handled **before** authentication, or the browser blocks the call |

### 7.10 Auth controller and DTOs

```java
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password) { }

public record LoginRequest(@NotBlank String username, @NotBlank String password) { }

public record AuthResponse(String token, String type) { }
```

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UserRepository users, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new DuplicateResourceException("Username already taken");          // 409
        }
        users.save(new AppUser(req.username(), passwordEncoder.encode(req.password()), Role.USER));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        UserDetails user = (UserDetails) auth.getPrincipal();
        return new AuthResponse(jwtService.generateToken(user), "Bearer");
    }
}
```

- Registration **never assigns `ADMIN`** from client input (over-posting risk). Create admins by a separate, protected process.
- `authenticate(...)` throws `BadCredentialsException` for a wrong username or password. Use one generic message so attackers cannot learn which part was wrong.

### 7.11 Extend the global exception handler (important gotcha)

If your `@RestControllerAdvice` has a catch-all `@ExceptionHandler(Exception.class)`, it will **swallow** security exceptions thrown from controllers or services and turn them into **500**. Add explicit handlers:

```java
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ApiError> badCredentials(BadCredentialsException ex) {
    return build(HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
}

@ExceptionHandler(AccessDeniedException.class)       // thrown by @PreAuthorize
public ResponseEntity<ApiError> denied(AccessDeniedException ex) {
    return build(HttpStatus.FORBIDDEN, "You do not have permission to do this", null);
}
```

(`AccessDeniedException` here is `org.springframework.security.access.AccessDeniedException`.)

### 7.12 Trying it out

```bash
# register -> 201
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"asha","password":"strongPass1"}'

# login -> 200 {"token":"eyJhbGciOi...","type":"Bearer"}
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"asha","password":"strongPass1"}'

# no token -> 401
curl -i http://localhost:8080/api/v1/employees

# with token -> 200
curl -H "Authorization: Bearer eyJhbGciOi..." http://localhost:8080/api/v1/employees

# USER token on an admin URL -> 403
curl -i -H "Authorization: Bearer eyJhbGciOi..." http://localhost:8080/api/v1/admin/users
```

---

## 8. Authorization in Practice

### 8.1 URL-based rules

In the filter chain (section 7.9): `permitAll`, `authenticated`, `hasRole`. Best for broad rules (public vs private, admin areas).

### 8.2 Method-level rules (`@PreAuthorize`)

Best for **fine-grained** and **business** rules close to the code.

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @GetMapping                                           // any authenticated user
    public PageResponse<EmployeeResponse> list(...) { ... }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> create(...) { ... }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { ... }
}
```

### 8.3 Ownership checks (prevent IDOR)

A logged-in user must not read **another user's** data just by changing an ID in the URL (**Insecure Direct Object Reference**, a top OWASP risk).

```java
@GetMapping("/orders/{id}")
public OrderResponse getOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
    Order order = service.find(id);
    if (!order.getOwnerUsername().equals(user.getUsername())
            && user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        throw new AccessDeniedException("Not your order");
    }
    return OrderResponse.from(order);
}
```

Or with an expression:

```java
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
public Profile profile(String username) { ... }
```

**Rule:** authentication ("are you logged in?") is **not** authorization ("may you access *this* record?"). Check ownership on every record access.

### 8.4 Role hierarchy (optional)

Let `ADMIN` automatically include `USER` permissions:

```java
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
}
```

(The factory method is available in recent Spring Security 6.x; older versions use `setHierarchy`.)

### 8.5 401 or 403?

- Missing, invalid or expired token: **401**.
- Valid token but wrong role or not the owner: **403**.

---

## 9. CORS and CSRF

### CORS (recap from the REST notes)

Required when React (`localhost:3000`) calls Spring (`localhost:8080`). With Spring Security, configure it **in the security chain** (`.cors(...)` plus a `CorsConfigurationSource` bean) so preflight `OPTIONS` requests are handled **before** authentication. Otherwise the browser's preflight is rejected, and you see a CORS error even though the real cause is security.

### CSRF

> **Cross-Site Request Forgery:** a malicious site tricks your browser into sending a request to a site where you are logged in, and the browser **automatically attaches your cookies**.

**Analogy:** someone slides a **cheque** in front of you while you are logged into your bank, and your signature (the cookie) is applied automatically.

| Authentication style | CSRF risk? | Setting |
|---|---|---|
| **Cookie / session** based | **Yes** (cookies are automatic) | Keep CSRF protection **enabled** |
| **Bearer token in a header** (JWT) | **No** (the browser does not attach it automatically) | `csrf.disable()` is acceptable |

**Symptom of forgetting to disable it in a stateless API:** `GET` works but `POST`, `PUT` and `DELETE` return **403**.

If you store the JWT in an **`HttpOnly` cookie**, the browser *does* send it automatically, so you must use CSRF protection (or `SameSite` cookies plus other measures).

---

## 10. Refresh Tokens, Logout, Revocation

### The pattern

```
login            -> access token (15 min) + refresh token (7 days)
API calls        -> access token
access expires   -> POST /api/auth/refresh with the refresh token -> new access token
refresh expires  -> user logs in again
```

### Refresh-token storage

- Generate a **random opaque string** (UUID or 256-bit random), **not** a JWT.
- Store a **hash** of it in the database with the user, expiry and revoked flag (treat it like a password).
- **Rotation:** on every refresh, **issue a new refresh token and invalidate the old one**. If an old one is reused, assume theft and revoke the family.

```java
@Entity
public class RefreshToken {
    @Id @GeneratedValue private Long id;
    private String tokenHash;
    private String username;
    private Instant expiresAt;
    private boolean revoked;
}

@PostMapping("/refresh")
public AuthResponse refresh(@RequestBody RefreshRequest req) {
    RefreshToken stored = refreshService.verify(req.refreshToken());     // checks hash, expiry, revoked
    UserDetails user = userDetailsService.loadUserByUsername(stored.getUsername());
    refreshService.revoke(stored);                                       // rotate
    String newRefresh = refreshService.issue(user.getUsername());
    return new AuthResponse(jwtService.generateToken(user), newRefresh);
}
```

### Logout with stateless tokens

There is no server session to delete. Options:

| Approach | Effect |
|---|---|
| **Client deletes the tokens** | Simplest; the access token stays valid until it expires (hence the short life) |
| **Revoke the refresh token** server-side | Stops the user getting new access tokens |
| **Deny-list** (blocklist `jti` in Redis until expiry) | Immediate revocation, at the cost of a lookup per request |

---

## 11. The Client Side (React)

### Login and authenticated calls

```javascript
// login
const res = await fetch("http://localhost:8080/api/auth/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ username, password }),
});
if (!res.ok) throw new Error("Login failed");
const { token } = await res.json();
localStorage.setItem("token", token);

// authenticated request
async function api(url, options = {}) {
  const token = localStorage.getItem("token");
  const res = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
  if (res.status === 401) {                  // expired or invalid token
    localStorage.removeItem("token");
    window.location.href = "/login";
    return;
  }
  if (!res.ok) throw new Error((await res.json()).message);
  return res.json();
}
```

With **Axios**, an **interceptor** adds the header to every request and handles `401` in one place.

### Protecting routes

Hide pages for logged-out users (a redirect component that checks for a token). **This is a usability feature, not security.** The real enforcement is always the **server**, since anyone can edit client code.

### Where to store the token

| Storage | Pros | Cons |
|---|---|---|
| **`localStorage`** | Simple; no CSRF | Readable by any injected script (**XSS** can steal it) |
| **Memory only** (React state) | Not accessible after a page reload by other scripts; safest from XSS persistence | Lost on refresh (combine with a refresh cookie) |
| **`HttpOnly`, `Secure`, `SameSite` cookie** | JavaScript **cannot read it**, so XSS cannot steal it | Needs CSRF protection and same-site/CORS care |

A common secure pattern: **short-lived access token in memory, refresh token in an `HttpOnly` cookie.** For learning projects, `localStorage` is common, but know the trade-off and prevent XSS (never inject unsanitized HTML; use a content security policy).

### Decoding a token in the client

Reading the payload (for the username or role to adjust the UI) is fine, since it is just Base64. **Never trust it for security decisions.**

---

## 12. OAuth2, OpenID Connect and Microservices

### OAuth2 in one picture

> **OAuth2** lets an app get **limited access** to a user's resources **without** the user sharing their password with that app. **OpenID Connect (OIDC)** adds a standard **login/identity** layer on top.

**Analogy:** a **hotel valet key**. It starts the car and opens the driver's door but not the trunk or glovebox. You hand over limited access, not your master key.

| Role | Example ("Login with Google") |
|---|---|
| **Resource owner** | The user |
| **Client** | Your application |
| **Authorization server** | Google's login service (or Keycloak, Auth0, Okta) |
| **Resource server** | The API that serves protected data |

The standard browser flow is **Authorization Code with PKCE**: the user logs in at the authorization server, and your app receives a code that it exchanges for tokens.

### The production-grade way to validate JWTs in Spring

Instead of a hand-written filter, you can configure your API as an **OAuth2 Resource Server**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```java
http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
```

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://auth.example.com/realms/demo
```

Spring downloads the issuer's **public keys** and validates signature, expiry and issuer for you. The hand-written approach in section 7 is excellent for **learning how it works**; for real systems, prefer a proven authorization server (**Keycloak**, Auth0, Spring Authorization Server) with a resource server.

### Microservices tie-in

```
Client -> API Gateway (validates the JWT once) -> customer-service
                                               -> account-service
```

- Validate the token at the **gateway**, and/or make each service a **resource server**. Never trust internal calls blindly.
- Use **RS256**: the issuer signs with a private key; every service verifies with the public key.
- **Propagate the token** on service-to-service calls (Feign interceptor):

```java
@Bean
public RequestInterceptor bearerTokenInterceptor() {
    return template -> {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String auth = attrs.getRequest().getHeader("Authorization");
            if (auth != null) template.header("Authorization", auth);
        }
    };
}
```

---

## 13. Testing Secured Endpoints

Add `spring-security-test`.

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired MockMvc mvc;

    @Test
    void anonymousIsUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/employees"))
           .andExpect(status().isUnauthorized());                    // 401
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotUseAdminEndpoint() throws Exception {
        mvc.perform(get("/api/v1/admin/users"))
           .andExpect(status().isForbidden());                       // 403
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUseAdminEndpoint() throws Exception {
        mvc.perform(get("/api/v1/admin/users"))
           .andExpect(status().isOk());
    }

    @Test
    void loginReturnsToken() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"asha","password":"strongPass1"}
                    """))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.token").exists());
    }
}
```

- `@WithMockUser` places a fake authenticated user in the context (no token needed).
- For cookie/form-based apps, add `.with(csrf())` to state-changing requests.
- Test the **negative cases**: no token, invalid token, expired token, wrong role, someone else's data.

---

## 14. Common Attacks and Defenses

| Attack | What it is | Defense |
|---|---|---|
| **SQL injection** | Malicious input changes a query | JPA/`PreparedStatement` parameters; never concatenate SQL |
| **XSS** (cross-site scripting) | Injected script runs in a user's browser | Escape output (Thymeleaf `th:text`), avoid `innerHTML`, Content Security Policy, `HttpOnly` cookies |
| **CSRF** | Forged request using automatic cookies | CSRF tokens, `SameSite` cookies; N/A for header tokens |
| **Broken access control / IDOR** | Accessing other users' records by changing an ID | Ownership checks on every record, deny by default |
| **Brute force / credential stuffing** | Guessing passwords | Rate limiting, lockout or delay, strong hashing, MFA |
| **Weak password storage** | Leaked database exposes passwords | BCrypt/Argon2 with salt |
| **JWT attacks** | `alg:none`, weak secret, algorithm confusion, theft | Pin algorithm, strong secret, short expiry, HTTPS |
| **Sensitive data exposure** | Secrets in logs, responses or Git | HTTPS, DTOs, no secrets in code, scrub logs |
| **Mass assignment (over-posting)** | Client sets fields like `role` | Request DTOs without privileged fields |
| **Man-in-the-middle** | Traffic intercepted | **HTTPS only**, HSTS |
| **Clickjacking** | Page framed by a hostile site | Spring's default security headers (`X-Frame-Options`) |
| **Verbose errors** | Stack traces reveal internals | Generic messages, log details server-side |

**Principles:** least privilege, deny by default, validate all input, defense in depth, and keep dependencies updated.

---

## 15. Debugging 401 and 403

| Symptom | Likely cause | Fix |
|---|---|---|
| **401 on `/api/auth/login`** | Path not in `permitAll` | Add `requestMatchers("/api/auth/**").permitAll()` |
| **`GET` works, `POST`/`PUT`/`DELETE` give 403** | CSRF protection still enabled on a stateless API | `csrf(c -> c.disable())` |
| **403 for an ADMIN user** | Role prefix mismatch (`ADMIN` vs `ROLE_ADMIN`), or role missing from authorities | Store `ROLE_ADMIN`; use `hasRole("ADMIN")` |
| **401 with a "valid" token** | Expired; wrong secret or key; missing `Bearer ` prefix; whitespace; clock skew | Check the header format, expiry and secret |
| **Anonymous request returns 403, not 401** | No `authenticationEntryPoint` configured | Configure it (section 7.9) |
| **Redirect (302) to an HTML `/login`** | `formLogin` still enabled for an API | Do not configure `formLogin`; set the entry point |
| **Browser shows a CORS error, Postman works** | CORS not configured in the security chain, preflight rejected | `.cors(...)` + `CorsConfigurationSource` |
| **Login failure returns 500** | Catch-all `@ExceptionHandler(Exception.class)` swallows `BadCredentialsException` | Add specific security handlers (section 7.11) |
| **`@PreAuthorize` has no effect** | Missing `@EnableMethodSecurity`; self-invocation (proxy bypass); private method | Enable it; call through another bean |
| **Everything is allowed** | `anyRequest().permitAll()`; matcher order wrong (first match wins) | Reorder rules; specific first |
| **Roles empty in the principal** | Authorities not loaded or wrongly named | Check `UserDetailsService` and the claim mapping |
| **Token works then suddenly fails** | Expired (access token life is short) | Refresh flow or re-login |
| **`JwtException: signature does not match`** | Token created with a different secret than the verifier | Same key for sign and verify; check the environment variable |
| **`WeakKeyException`** | HS256 key shorter than 256 bits | Use a 32-byte (or longer) random key |

**Tip:** set `logging.level.org.springframework.security=DEBUG` to see which filter handled the request and which rule decided.

---

## 16. Common Mistakes

| Mistake | Problem | Fix |
|---|---|---|
| Storing plain-text or MD5/SHA passwords | Trivially cracked after a leak | BCrypt or Argon2 |
| Hard-coding the JWT secret or committing it | Anyone can forge tokens | Environment variable or secrets manager |
| Short or guessable secret | Brute-forceable | 256-bit or longer random key |
| Putting sensitive data in the JWT | Payload is readable | Minimal claims |
| Very long-lived access tokens | Stolen token works for ages | 5 to 30 minutes plus refresh |
| Trusting the client (hiding buttons) for security | Trivially bypassed | Enforce on the server |
| Authenticating but not checking ownership | IDOR | Per-record authorization |
| Accepting a `role` field from request DTOs | Privilege escalation | Server decides roles |
| `permitAll` on too much | Open endpoints | Deny by default; `anyRequest().authenticated()` |
| Wrong rule order | A broad rule shadows a specific one | Specific first |
| Using `WebSecurityConfigurerAdapter`, `antMatchers`, `javax.*` | Does not compile on Spring Security 6 | Modern APIs (section 1.4) |
| CSRF left on for a stateless API | POST/PUT/DELETE blocked | Disable it (header tokens only) |
| CSRF disabled while using cookie auth | CSRF vulnerability | Keep it enabled |
| `allowedOrigins("*")` with credentials | Not allowed and unsafe | List specific origins |
| JWT filter throws on invalid tokens | Inconsistent responses | Leave the context empty and let the entry point answer |
| Catch-all exception handler swallowing security exceptions | 500 instead of 401/403 | Add specific handlers |
| Different errors for "user not found" vs "wrong password" | Username enumeration | One generic message |
| Logging passwords or tokens | Credential leak | Never log them |
| Forgetting HTTPS | Tokens sniffed in transit | HTTPS everywhere |
| `localStorage` token with unsanitized HTML | XSS steals the token | Sanitize, CSP, or an `HttpOnly` cookie |
| Same secret for dev and prod | Dev leak compromises prod | Separate secrets per environment |
| Treating JWT as encryption | False sense of privacy | JWT is signed, not encrypted |
| Using JWT where sessions would be simpler | Needless complexity and no revocation | Choose per architecture |

---

## 17. Interview Questions

**1. Authentication vs authorization?** Authentication verifies who you are; authorization decides what you may do.

**2. What is Spring Security?** A framework providing authentication, authorization and protection against common attacks, implemented as a chain of servlet filters.

**3. What happens when you add `spring-boot-starter-security`?** All endpoints are secured with a generated password for user `user`, form login and HTTP Basic, CSRF protection and security headers.

**4. What is the `SecurityFilterChain`?** The bean defining which filters run and which access rules apply; it replaces `WebSecurityConfigurerAdapter`.

**5. How does the authentication flow work?** Credentials go to `AuthenticationManager`, then `AuthenticationProvider` (`DaoAuthenticationProvider`), which uses `UserDetailsService` to load the user and `PasswordEncoder` to verify; on success an `Authentication` is stored in the `SecurityContext`.

**6. What is `UserDetailsService`?** An interface with `loadUserByUsername`, which you implement to load users from your database.

**7. What is `SecurityContextHolder`?** It stores the current user's `Authentication` for the running request (per thread).

**8. `hasRole` vs `hasAuthority`?** `hasRole("ADMIN")` checks the authority `ROLE_ADMIN`; `hasAuthority` checks the exact string.

**9. Why hash passwords, and why BCrypt?** Hashing is one-way, so leaked data does not reveal passwords. BCrypt adds a salt and is deliberately slow to resist brute force.

**10. Hashing vs encryption vs encoding?** Hashing is irreversible; encryption is reversible with a key; encoding is a reversible format change with no secrecy.

**11. What is a JWT?** A signed, compact token carrying claims (header, payload, signature), used for stateless authentication.

**12. What are the three parts of a JWT?** Header (algorithm), payload (claims), signature.

**13. Is a JWT encrypted?** No, only Base64-encoded and signed. Anyone can read it, but only the key holder can create a valid one.

**14. How is a JWT verified?** The server recomputes the signature with its key and compares, then checks `exp` and other claims, with no database lookup.

**15. HS256 vs RS256?** HS256 uses one shared secret; RS256 uses a private key to sign and a public key to verify, suiting multiple verifying services.

**16. Access vs refresh token?** Short-lived token for API calls vs long-lived, server-stored token used to get new access tokens.

**17. How do you log out with JWT?** Delete the client tokens, revoke the refresh token, and optionally deny-list the access token.

**18. Why is JWT revocation hard?** The server holds no state; a valid token remains valid until it expires.

**19. Session vs JWT?** Sessions are stateful, easy to revoke and cookie-based. JWTs are stateless and scale easily but are harder to revoke.

**20. What is CSRF, and why disable it for JWT APIs?** An attack exploiting automatically attached cookies. Header-based tokens are not sent automatically, so the risk does not apply.

**21. When must CSRF stay enabled?** For cookie/session-based authentication.

**22. What is CORS, and how does it interact with Spring Security?** A browser restriction on cross-origin calls; it must be configured in the security chain so preflight requests are handled before authentication.

**23. What does `OncePerRequestFilter` do?** Guarantees the filter executes once per request.

**24. Why `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)`?** So the token establishes authentication before the default filters and authorization run.

**25. What does `SessionCreationPolicy.STATELESS` do?** Spring Security neither creates nor uses an `HttpSession`.

**26. 401 vs 403?** 401: not authenticated (or invalid token). 403: authenticated but not permitted.

**27. Why might `GET` work but `POST` return 403?** CSRF protection is enabled on a stateless API.

**28. `@PreAuthorize` vs URL rules?** URL rules are broad and centralized; `@PreAuthorize` is method-level and can use business expressions such as ownership.

**29. What is IDOR, and how do you prevent it?** Accessing others' records by changing an ID; prevent it by checking ownership on every access.

**30. Why do security filter exceptions not reach `@RestControllerAdvice`?** They occur before the `DispatcherServlet`; use `AuthenticationEntryPoint` and `AccessDeniedHandler`.

**31. What is OAuth2 vs OIDC?** OAuth2 delegates limited access to resources; OIDC adds standardized authentication and identity on top.

**32. What is a resource server?** The API that validates access tokens (JWTs) and serves protected resources.

**33. How do you secure microservices with JWT?** Validate at the gateway and/or in each service, use RS256 with public keys, and propagate the token between services.

**34. Where should a SPA store tokens?** Trade-offs: `localStorage` (XSS risk), memory (lost on refresh), `HttpOnly` cookie (needs CSRF defenses). A common secure choice is an access token in memory plus a refresh token in an `HttpOnly` cookie.

**35. What claims should you validate?** Signature, `exp`, and ideally `iss` and `aud`.

**36. What is the `alg: none` attack?** A forged unsigned token accepted by a naive library; prevented by requiring signatures and pinning the algorithm.

**37. Why must the HS256 secret be long and random?** Short secrets can be brute-forced offline from a captured token.

**38. How do you prevent users registering as ADMIN?** Never accept a role from the client; assign roles on the server.

---

## 18. Cheat Sheet

**Flow**

```
Register (BCrypt hash) -> Login (AuthenticationManager) -> JWT issued
Request + "Authorization: Bearer <jwt>" -> JwtFilter validates -> SecurityContext set
-> AuthorizationFilter checks rules -> Controller
```

**Config essentials (Spring Security 6)**

```java
http.csrf(c -> c.disable())
    .cors(Customizer.withDefaults())
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    .authorizeHttpRequests(a -> a
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
    .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

| Need | Use |
|---|---|
| Load users | `UserDetailsService` |
| Hash passwords | `BCryptPasswordEncoder` |
| Authenticate credentials | `AuthenticationManager.authenticate(...)` |
| Create / read JWT | JJWT `Jwts.builder()...` / `Jwts.parser().verifyWith(key)...` |
| Current user | `@AuthenticationPrincipal`, `SecurityContextHolder` |
| URL access rules | `authorizeHttpRequests` |
| Method rules | `@EnableMethodSecurity` + `@PreAuthorize` |
| JSON 401 / 403 | `AuthenticationEntryPoint`, `AccessDeniedHandler` |
| Test | `@WithMockUser`, `spring-security-test` |

**Status codes**

| Situation | Code |
|---|---|
| No or invalid or expired token | **401** |
| Valid token, wrong role or not the owner | **403** |
| Duplicate username on register | **409** |
| Validation failure | **400** |

**Golden rules**

```
1. Hash passwords with BCrypt; never store or log them.
2. Secrets from the environment; 256-bit+ random keys.
3. Short-lived access tokens + refresh tokens; HTTPS everywhere.
4. A JWT is signed, NOT encrypted: keep it minimal.
5. Enforce everything on the server; UI hiding is cosmetic.
6. Deny by default; check ownership on every record.
7. Stateless API: disable CSRF, STATELESS sessions; cookies need CSRF.
8. Handle 401/403 with an entry point and denied handler; add security exception handlers to your advice.
```

---

## 19. Practice Tasks with Solutions

**1. Basic security first.** Add the starter, run the app, log in with the generated password, then replace it with two in-memory users (`USER`, `ADMIN`) and protect `/admin/**` (section 5).

**2. Implement the full JWT flow** from section 7: register, login, and a protected `GET /api/v1/employees`. Verify the `curl` sequence in section 7.12.

**3. Admin-only delete.** Make `DELETE /api/v1/employees/{id}` require `ADMIN`:

```java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void delete(@PathVariable Long id) { service.delete(id); }
```

**4. Ownership check.** Add an `owner` field to a `Note` entity and make `GET /notes/{id}` return **403** for other users' notes (section 8.3).

**5. JSON 401/403.** Confirm anonymous requests return `401` JSON and a `USER` calling an admin URL returns `403` JSON (sections 7.8 and 7.9).

**6. Current user endpoint.**

```java
@GetMapping("/api/auth/me")
public Map<String, Object> me(@AuthenticationPrincipal UserDetails user) {
    return Map.of(
        "username", user.getUsername(),
        "roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
}
```

(Place it **outside** the `permitAll` auth paths so it requires a valid token: for example `/api/me`.)

**7. Refresh tokens.** Implement `POST /api/auth/refresh` with rotation (section 10).

**8. Security tests.** Write the four tests in section 13 (anonymous 401, wrong role 403, admin 200, login returns a token).

**9. React login.** Build a login form that stores the token, attaches it to API calls, and redirects to `/login` on a `401`. Show an admin menu only for admins (decoded from the token), and confirm the server still rejects non-admin calls.

**10. Password policy.** Reject passwords shorter than 8 characters (validation on `RegisterRequest`) and rate-limit login attempts per IP (for example with a simple in-memory counter or a library such as Bucket4j).

### Challenge questions

1. A user logs in successfully, but every `POST` returns 403 while `GET` works. What is the likely cause and fix?
2. An access token is valid for 24 hours. A user's account is disabled at hour 1. What happens, and how do you improve it?
3. Why is it safe for a React page to read the role from the decoded JWT to hide a menu, but unsafe to rely on it for authorization?

**Answers**

1. **CSRF protection is still enabled** on a stateless API (it blocks state-changing methods without a CSRF token). Fix: `csrf(c -> c.disable())` for a header-token API.
2. The token **keeps working until it expires**, because the server holds no state. Improve by shortening the access-token life (5 to 15 minutes), using refresh tokens that you can revoke, and having the filter **reload the user from the database** (so a disabled account is rejected) or checking a deny-list.
3. Reading it only changes **what the UI shows**. The user controls the browser and can edit any client code or craft requests directly. The **server** must verify the token signature and authorize every request itself.

---

*End of notes.*
