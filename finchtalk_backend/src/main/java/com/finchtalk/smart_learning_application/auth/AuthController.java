package com.finchtalk.smart_learning_application.auth;

import com.finchtalk.smart_learning_application.model.User;
import com.finchtalk.smart_learning_application.repository.UserRepository;
import com.finchtalk.smart_learning_application.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(req.username(), passwordEncoder.encode(req.password()), req.role());
        userRepository.save(user);

        // Return token immediately for fast dev
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());

        return Map.of("token", token);


    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        boolean ok = passwordEncoder.matches(req.password(), user.getPasswordHash());
        if (!ok) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());

        return Map.of("token", token);
    }

    public record RegisterRequest(
            @jakarta.validation.constraints.NotBlank String username,
            @jakarta.validation.constraints.NotBlank String password,
            User.Role role
    ) {
    }

    public record LoginRequest(
            @jakarta.validation.constraints.NotBlank String username,
            @jakarta.validation.constraints.NotBlank String password
    ) {
    }
}

