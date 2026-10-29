package com.example.bank.auth;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
  return jwt.generate(u.getUsername(),u.getRole(),u.getCustomerId());
 }
 @GetMapping("/validate") public String validate(@RequestParam String token){
  try{return "Token is valid for "+jwt.parse(token).getSubject();}
  catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired token");}
 }

 // ---------------- admin only: who may do what ----------------

 /** Lists every user (no passwords). ADMIN only. */
 @GetMapping("/admin/users") @Transactional(readOnly=true)
 public List<UserSummary> listUsers(@RequestHeader(value=HttpHeaders.AUTHORIZATION,required=false) String authorization){
  requireAdmin(authorization);
  return users.findAll().stream().map(UserSummary::from).collect(Collectors.toList());
 }

 /**
  * Sets a user's role and the customer record the login is linked to. ADMIN only.
  * A CUSTOMER needs a customerId before the services will show them any account; staff do not.
  * The new role applies the next time the user signs in (tokens already issued keep their old role until they expire).
  */
 @PutMapping("/admin/users/{username}") @Transactional
 public UserSummary updateUser(@PathVariable String username,@Valid @RequestBody UserAdminRequest r,
   @RequestHeader(value=HttpHeaders.AUTHORIZATION,required=false) String authorization){
  Claims caller=requireAdmin(authorization);
  UserCredential u=users.findByUsername(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User "+username+" does not exist"));
  Role newRole=Role.valueOf(r.role());
  if(u.getUsername().equals(caller.getSubject())&&newRole!=Role.ADMIN)
   throw new ResponseStatusException(HttpStatus.CONFLICT,"You cannot remove your own admin role");
  u.setRole(newRole); u.setCustomerId(r.customerId());
  return UserSummary.from(users.save(u));
 }

 /** The caller's token must be valid and carry the ADMIN role; the auth service checks this itself. */
 private Claims requireAdmin(String authorization){
  if(authorization==null||!authorization.startsWith("Bearer ")) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Missing Bearer token");
  Claims claims;
  try{claims=jwt.parse(authorization.substring(7).trim());}
  catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or expired token");}
  if(!"ADMIN".equals(claims.get("role",String.class))) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only an administrator can do this");
  return claims;
 }
}
