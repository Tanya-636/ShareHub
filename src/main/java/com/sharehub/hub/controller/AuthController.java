package com.sharehub.hub.controller;

import com.sharehub.hub.dto.UserRegistrationRequest; // <-- NEW DTO IMPORT
import com.sharehub.hub.entity.User;
import com.sharehub.hub.repository.UserRepository;
import com.sharehub.hub.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationRequest request) {

        // Build the User entity from the DTO
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName()) // <-- Uses the actual name collected by the UI
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // The problematic 'if (user.getName() == null) user.setName("Anonymous");' is now safely bypassed.

        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());
        return Map.of("token", token);
    }
}