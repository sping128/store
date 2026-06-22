package com.codewithmosh.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dtos.LoginResponse;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.requests.LoginRequest;
import com.codewithmosh.store.requests.RefreshRequest;
import com.codewithmosh.store.services.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        var refreshToken = jwtService.generateRefreshToken(loginRequest.getUsername());
        var accessToken = jwtService.generateAccessToken(loginRequest.getUsername());
        return ResponseEntity.ok().body(new LoginResponse(accessToken, refreshToken));
    }

    @Valid
    @PostMapping("refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        try {
            var username = jwtService.extractUsername(request.getRefreshToken());
            var tokenType = jwtService.extractType(request.getRefreshToken());
            if (!tokenType.equals(JwtService.TOKEN_TYPE_REFRESH))
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            var refreshToken = jwtService.generateRefreshToken(username);
            var accessToken = jwtService.generateAccessToken(username);
            return ResponseEntity.ok().body(new LoginResponse(accessToken, refreshToken));
        } catch (JwtException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
