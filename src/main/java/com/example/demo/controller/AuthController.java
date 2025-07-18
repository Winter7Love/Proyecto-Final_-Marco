package com.example.demo.controller;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.security.payload.AuthRequest;
import com.example.demo.security.payload.AuthResponse;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.security.core.AuthenticationException; // Importar AuthenticationException


/**
 * Controlador REST para la autenticación de usuarios (inicio de sesión y registro).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Declarar logger

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository; // Asumiendo que tiene un RolRepository
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        // ¡NUEVO LOG PARA DEPURACIÓN!
        logger.info("Intentando autenticar usuario: {}", loginRequest.getUsername());
        logger.debug("Contraseña recibida (sin encriptar): {}", loginRequest.getPassword()); // ¡CUIDADO! NO USAR EN PRODUCCIÓN

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            // ¡NUEVO LOG PARA DEPURACIÓN!
            logger.error("Fallo de autenticación para el usuario {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        logger.info("Usuario {} autenticado exitosamente. JWT generado.", loginRequest.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt, "Bearer", loginRequest.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest signUpRequest) {
        if (usuarioRepository.existsByNombreUsuario(signUpRequest.getUsername())) { // Usar existsBy
            return ResponseEntity
                    .badRequest()
                    .body("Error: ¡El nombre de usuario ya está en uso!");
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) { // Usar existsBy
            return ResponseEntity
                    .badRequest()
                    .body("Error: ¡El email ya está en uso!");
        }

        // Crear nueva cuenta de usuario
        Usuario user = new Usuario();
        user.setNombreUsuario(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setNombreCompleto(signUpRequest.getNombreCompleto());
        user.setTelefono(signUpRequest.getTelefono());
        user.setDireccion(signUpRequest.getDireccion());

        // Rol por defecto para nuevos usuarios (ej. "USER" o "MIEMBRO")
        Rol userRole = rolRepository.findByNombreRol("MIEMBRO") // Asegúrese de que el rol "MIEMBRO" exista en su BD
                .orElseThrow(() -> new RuntimeException("Error: Rol 'MIEMBRO' no encontrado."));
        user.setRol(userRole);

        usuarioRepository.save(user);

        return ResponseEntity.ok("Usuario registrado exitosamente!");
    }
}
