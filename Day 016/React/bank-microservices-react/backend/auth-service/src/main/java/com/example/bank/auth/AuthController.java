package com.example.bank.auth;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/auth")
public class AuthController {
 private final UserCredentialRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthController(UserCredentialRepository u,PasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
 public String register(@Valid @RequestBody RegisterRequest r){
  if(users.existsByUsername(r.username())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username already exists");
  users.save(new UserCredential(r.username(),encoder.encode(r.password()))); return "User registered successfully";
 }
 @PostMapping("/token") public String token(@Valid @RequestBody AuthRequest r){
  UserCredential u=users.findByUsername(r.username()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password"));
  if(!encoder.matches(r.password(),u.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
  return jwt.generate(u.getUsername());
 }
 @GetMapping("/validate") public String validate(@RequestParam String token){
  try{return "Token is valid for "+jwt.parse(token).getSubject();}
  catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired token");}
 }
}
