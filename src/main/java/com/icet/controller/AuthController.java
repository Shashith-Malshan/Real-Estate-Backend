package com.icet.controller;

import com.icet.model.dto.RoleSwitchRequestDTO;
import com.icet.model.dto.UserLoginDTO;
import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO dto) {
        UserResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO dto) {
        UserResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get role IDs assigned to a user
     * GET /api/auth/users/{userId}/roles
     */
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<Long>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.getAvailableRoleIds(userId));
    }

    /**
     * Switch the active role for a user
     * POST /api/auth/switch-role
     */
    @PostMapping("/switch-role")
    public ResponseEntity<UserResponseDTO> switchRole(@RequestBody RoleSwitchRequestDTO dto) {
        return ResponseEntity.ok(authService.switchActiveRole(dto));
    }
}

