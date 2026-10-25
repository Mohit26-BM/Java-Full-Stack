package com.example.bank.auth;
import jakarta.persistence.*;
@Entity @Table(name="user_credentials", uniqueConstraints=@UniqueConstraint(columnNames="username"))
public class UserCredential {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=80) private String username;
 @Column(nullable=false) private String password;
 public UserCredential(){}
 public UserCredential(String u,String p){username=u;password=p;}
 public Long getId(){return id;} public String getUsername(){return username;} public String getPassword(){return password;}
}
