package com.tourplanner.service;

import com.tourplanner.dto.AuthRequest;
import com.tourplanner.dto.AuthResponse;
import com.tourplanner.exception.InvalidCredentialsException;
import com.tourplanner.model.User;
import com.tourplanner.repository.UserRepository;
import com.tourplanner.security.JwtService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed – unknown email='{}'", request.email());
                    return new InvalidCredentialsException("Invalid credentials");
                });
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed – wrong password for email='{}'", request.email());
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return new AuthResponse(jwtService.generateToken(user));
    }
}
