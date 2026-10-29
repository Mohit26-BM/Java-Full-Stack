package com.example.bank.auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${security.jwt.secret:}") String secret,@Value("${security.jwt.expiration-ms:3600000}") long expiration){
  if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalStateException("security.jwt.secret (JWT_SECRET) must be set and at least 32 characters long");
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expiration=expiration;
 }
 /** The token carries the username (subject), the role, and for linked customers the customer id. The other services trust these claims. */
 public String generate(String username,Role role,Long customerId){
  Date now=new Date();
  JwtBuilder b=Jwts.builder().subject(username).claim("role",role.name()).issuedAt(now).expiration(new Date(now.getTime()+expiration));
  if(customerId!=null) b.claim("customerId",customerId);
  return b.signWith(key).compact();
 }
 public Claims parse(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
}
