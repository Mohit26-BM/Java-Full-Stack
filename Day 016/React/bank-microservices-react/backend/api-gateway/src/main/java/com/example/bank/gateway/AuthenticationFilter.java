package com.example.bank.gateway;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
 private final SecretKey key;
 public AuthenticationFilter(@Value("${security.jwt.secret}") String secret){super(Config.class);key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 public static class Config{}
 @Override public GatewayFilter apply(Config config){return (exchange,chain)->{
  if(exchange.getRequest().getMethod()==HttpMethod.OPTIONS)return chain.filter(exchange);
  String header=exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
  if(header==null||!header.startsWith("Bearer "))return reject(exchange,"Missing Bearer token");
  try{Jwts.parser().verifyWith(key).build().parseSignedClaims(header.substring(7).trim());return chain.filter(exchange);}
  catch(Exception ex){return reject(exchange,"Invalid or expired token");}
 };}
 private Mono<Void> reject(ServerWebExchange ex,String message){
  ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
  ex.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE,"text/plain");
  return ex.getResponse().writeWith(Mono.just(ex.getResponse().bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8))));
 }
}
