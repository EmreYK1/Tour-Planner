package com.tourplanner.service;

import com.tourplanner.dto.AuthRequest;
import com.tourplanner.dto.AuthResponse;

public interface AuthService {

    // Registriert einen neuen User und gibt ein JWT zurück; 409 wenn E-Mail bereits vergeben.
    AuthResponse register(AuthRequest request);

    // Authentifiziert einen User und gibt ein JWT zurück; wirft InvalidCredentialsException bei Fehler.
    AuthResponse login(AuthRequest request);
}
