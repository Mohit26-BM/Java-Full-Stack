package com.example.bank.auth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserCredentialRepository extends JpaRepository<UserCredential,Long>{
 Optional<UserCredential> findByUsername(String username);
 boolean existsByUsername(String username);
}
