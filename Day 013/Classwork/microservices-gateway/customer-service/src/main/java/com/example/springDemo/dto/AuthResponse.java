package com.example.springDemo.dto;

import com.example.springDemo.entity.Role;

public record AuthResponse(String token, String email, String name, Role role, Long customerId) {
}
