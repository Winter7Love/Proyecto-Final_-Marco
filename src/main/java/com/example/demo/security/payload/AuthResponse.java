package com.example.demo.security.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Cuerpo de la respuesta para la autenticación exitosa, que contiene el JWT.
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer"; // Tipo de token
    private String username;
}