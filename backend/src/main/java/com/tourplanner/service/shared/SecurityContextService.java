package com.tourplanner.service.shared;

import com.tourplanner.model.User;
import com.tourplanner.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Kapselt den Zugriff auf den aktuell eingeloggten User; User-ID kommt direkt aus dem JWT (kein DB-Lookup).
@Service
public class SecurityContextService {

    private final UserRepository userRepository;

    public SecurityContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Liest die User-ID aus dem JWT – kein DB-Roundtrip.
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token
                && token.getDetails() instanceof Long userId) {
            return userId;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nicht authentifiziert");
    }

    // Lädt den vollständigen User aus der DB – nur verwenden wenn die Entity selbst benötigt wird.
    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User nicht gefunden"));
    }
}
