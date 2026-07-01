package com.buildsage.service;

import com.buildsage.dto.AuthDtos.LoginRequest;
import com.buildsage.dto.AuthDtos.LoginResponse;
import com.buildsage.repository.UserRepository;
import com.buildsage.security.CurrentUser;
import com.buildsage.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        var user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        CurrentUser principal = new CurrentUser(
                user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.isEnabled());
        return new LoginResponse(jwtService.issue(principal), user.getId(), user.getEmail(), user.getRole());
    }
}
