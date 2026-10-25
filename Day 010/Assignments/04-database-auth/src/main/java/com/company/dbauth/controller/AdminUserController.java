package com.company.dbauth.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.dbauth.dto.AuthRequests.EnabledRequest;
import com.company.dbauth.dto.AuthRequests.RolesRequest;
import com.company.dbauth.dto.UserResponses.UserResponse;
import com.company.dbauth.service.UserAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userAdminService.findAll();
    }

    @PatchMapping("/{id}/enabled")
    public UserResponse setEnabled(@PathVariable("id") Long id, @Valid @RequestBody EnabledRequest request,
                                   Authentication authentication) {
        return userAdminService.setEnabled(id, request.enabled(), authentication.getName());
    }

    @PostMapping("/{id}/unlock")
    public UserResponse unlock(@PathVariable("id") Long id) {
        return userAdminService.unlock(id);
    }

    @PutMapping("/{id}/roles")
    public UserResponse replaceRoles(@PathVariable("id") Long id, @Valid @RequestBody RolesRequest request) {
        return userAdminService.replaceRoles(id, request.roles());
    }
}
