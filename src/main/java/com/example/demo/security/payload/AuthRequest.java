package com.example.demo.security.payload;

import lombok.Getter;
import lombok.Setter;

/**
 * Cuerpo de la solicitud para la autenticación (inicio de sesión y registro).
 */
@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
    private String email; // Añadido para el registro
    private String nombreCompleto; // Añadido para el registro
    private String telefono; // Añadido para el registro
    private String direccion; // Añadido para el registro
}