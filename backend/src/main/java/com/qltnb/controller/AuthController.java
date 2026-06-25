package com.qltnb.controller;

import com.qltnb.dto.LoginRequest;
import com.qltnb.dto.LoginResponse;
import com.qltnb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest credentials) {
        LoginResponse response = authService.authenticate(credentials);
        return ResponseEntity.ok(response);
    }
}
