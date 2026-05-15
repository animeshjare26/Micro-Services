package com.creativeshouse.userservice.controllers;

import com.creativeshouse.userservice.dtos.request.LoginRequest;
import com.creativeshouse.userservice.dtos.request.RefreshTokenRequest;
import com.creativeshouse.userservice.dtos.request.SignupBusinessRequest;
import com.creativeshouse.userservice.dtos.request.SignupCreativeRequest;
import com.creativeshouse.userservice.dtos.response.JwtResponse;
import com.creativeshouse.userservice.security.service.AuthService;
import com.creativeshouse.userservice.utils.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<GenericResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(GenericResponse.success(jwtResponse));
    }

    @PostMapping("/signup/creative")
    public ResponseEntity<GenericResponse<String>> registerCreative(@Valid @RequestBody SignupCreativeRequest signUpRequest) {
        authService.registerCreative(signUpRequest);
        return ResponseEntity.ok(GenericResponse.success("Creative registered successfully!", "Success"));
    }

    @PostMapping("/signup/business")
    public ResponseEntity<GenericResponse<String>> registerBusiness(@Valid @RequestBody SignupBusinessRequest signUpRequest) {
        authService.registerBusiness(signUpRequest);
        return ResponseEntity.ok(GenericResponse.success("Business registered successfully! Verification Pending.", "Success"));
    }

    @PostMapping("/token")
    public ResponseEntity<GenericResponse<Map<String, String>>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        Map<String, String> tokenResponse = authService.refreshAccessToken(refreshTokenRequest.getToken());
        return ResponseEntity.ok(GenericResponse.success(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<String>> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(GenericResponse.success("Logged out successfully"));
    }
}
