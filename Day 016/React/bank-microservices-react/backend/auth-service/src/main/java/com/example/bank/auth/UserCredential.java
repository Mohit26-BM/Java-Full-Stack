package com.example.bank.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_credentials", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Role role = Role.CUSTOMER;

    /** The customer record this login belongs to (set by an admin). Null until linked. */
    @Column(name = "customer_id")
    private Long customerId;

    public UserCredential() {
    }

    /** Self-registered users are always plain customers, not yet linked to a customer record. */
    public UserCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserCredential(String username, String password, Role role, Long customerId) {
        this(username, password);
        this.role = role;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
