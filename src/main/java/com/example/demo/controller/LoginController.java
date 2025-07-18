package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// Eliminadas importaciones para el método temporal de reseteo:
// import org.springframework.web.bind.annotation.ResponseBody;
// import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador que maneja las operaciones de autenticación y registro de usuarios.
 * Incluye la visualización de las páginas de login y registro,
 * y el procesamiento del registro de nuevos usuarios.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UsuarioService usuarioService;

    /**
     * Constructor para inyectar las dependencias necesarias.
     * Spring se encargará de inyectar automáticamente UsuarioService y PasswordEncoder.
     * @param usuarioService Servicio para la lógica de negocio de usuarios.
     * @param passwordEncoder Codificador de contraseñas para encriptación y validación.
     */
    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- MÉTODOS PÚBLICOS (LOGIN, REGISTER) ---

    /**
     * Muestra la página de login.
     * @return El nombre de la vista "login".
     */
    @GetMapping("/login")
    public String showLoginPage() {
        logger.info("Accediendo a la página de login.");
        return "login";
    }

    /**
     * Muestra la página de registro de nuevos usuarios.
     * @param model El modelo de Spring MVC para añadir el objeto Usuario.
     * @return El nombre de la vista "register".
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        logger.info("Accediendo a la página de registro.");
        return "register";
    }

    /**
     * Procesa el registro de un nuevo usuario.
     * La contraseña se encriptará en el UsuarioService antes de guardarse.
     * @param usuario El objeto Usuario con los datos del formulario de registro.
     * @return Redirecciona a la página de login con un parámetro de éxito.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("usuario") Usuario usuario) {
        usuarioService.guardarUsuario(usuario);
        logger.info("Usuario registrado exitosamente: {}", usuario.getEmail());
        return "redirect:/login?registro_exitoso";
    }

    // --- MÉTODO TEMPORAL PARA RESETEAR LA CONTRASEÑA (ELIMINADO) ---
    // El método resetAlonsoPasswordTemporal() ha sido eliminado ya que su propósito de depuración ha concluido.
}
