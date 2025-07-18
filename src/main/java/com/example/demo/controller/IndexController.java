package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.example.demo.model.Tarea; // Asegúrate de que esta ruta sea correcta para tu clase Tarea
import com.example.demo.model.Usuario; // Asegúrate de que esta ruta sea correcta para tu clase Usuario
import com.example.demo.service.TareaService; // Asegúrate de que esta ruta sea correcta para tu TareaService
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final TareaService tareaService; // Inyección de tu servicio de tareas

    /**
     * Constructor para inyectar las dependencias necesarias.
     * 
     * @param tareaService Servicio para la gestión de tareas.
     */
    public IndexController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    /**
     * Maneja la ruta raíz de la aplicación ("/") y es el destino para usuarios
     * MIEMBRO autenticados.
     * Para ADMINs, redirige a /inventario.
     * Para usuarios no autenticados, redirige a /inmy-store.
     * 
     * @param model El modelo de Spring MVC para pasar el usuario a la vista.
     * @return La vista a la que redirigir o renderizar.
     */
    @GetMapping("/")
    // Esta ruta requiere autenticación, pero el filtro de seguridad la permite para
    // la redirección inicial.
    // La lógica interna decidirá si mostrar index.html o redirigir.
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado (no es "anonymousUser")
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String
                        && authentication.getPrincipal().equals("anonymousUser"))) {

            logger.info("Usuario autenticado en la raíz (/). Roles: {}", authentication.getAuthorities());

            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                logger.info("Redirigiendo a /inventario para ADMIN.");
                return "redirect:/inventario"; // Redirige al panel de administración para ADMIN
            } else {
                // Si es MIEMBRO autenticado, muestra index.html
                logger.info("Mostrando index.html para usuario MIEMBRO.");
                // Pasamos el objeto Usuario al modelo para que index.html pueda mostrar el
                // nombre
                model.addAttribute("usuario", (Usuario) authentication.getPrincipal());
                return "index"; // Muestra la vista 'index.html' para usuarios MIEMBRO
            }
        }

        // Si no hay un usuario logueado, redirigir a la página principal de la tienda
        // (pública)
        logger.info("Usuario no autenticado. Redirigiendo a /inmy-store (página pública).");
        return "redirect:/inmy-store"; // Redirige a la página principal de la tienda si no hay usuario logueado
    }

    /**
     * Muestra la página principal de la tienda ("/inmy-store").
     * Esta ruta es la "cara" de la aplicación y es accesible para todos
     * (autenticados o no).
     * Si el usuario está autenticado, su información se añade al modelo para
     * mostrar el nombre.
     * 
     * @param model El modelo de Spring MVC para pasar datos a la vista.
     * @return El nombre de la vista "principal".
     */
    @GetMapping("/inmy-store")
    // Esta página es pública, no necesita @PreAuthorize
    public String principal(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String
                        && authentication.getPrincipal().equals("anonymousUser"))) {
            // Si hay un usuario autenticado, lo añadimos al modelo para que el HTML pueda
            // mostrar su nombre
            model.addAttribute("usuario", (Usuario) authentication.getPrincipal());
            logger.info("Accediendo a /inmy-store. Usuario autenticado: {}",
                    ((Usuario) authentication.getPrincipal()).getNombreUsuario());
        } else {
            logger.info("Accediendo a /inmy-store. Usuario no autenticado.");
        }
        return "principal"; // Muestra la vista 'principal.html'
    }

    /**
     * Muestra la página de inventario (gestión de tareas).
     * Solo accesible para usuarios con el rol ADMIN.
     * 
     * @param model El modelo de Spring MVC.
     * @return El nombre de la vista "inventario" (o "gestion-tareas" si así la
     *         llamaste).
     */
    @GetMapping("/inventario")
    @PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden acceder
    public String inventario(Model model) {
        // Aquí deberías inyectar y usar tu TareaService real para obtener las tareas de
        // la BD
        List<Tarea> tareas = tareaService.obtenerTodasTareas(); // Asume que tienes este método en tu TareaService
        model.addAttribute("tareas", tareas);
        model.addAttribute("tarea", new Tarea()); // Crear un objeto vacío de tarea para el formulario de agregar

        // Inyectar el usuario autenticado para mostrar su nombre en la cabecera
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Usuario) {
            model.addAttribute("usuario", (Usuario) authentication.getPrincipal());
            logger.info("Mostrando página de inventario para ADMIN. Usuario: {}",
                    ((Usuario) authentication.getPrincipal()).getNombreUsuario());
        } else {
            logger.warn(
                    "Acceso a /inventario sin usuario ADMIN autenticado. Esto no debería ocurrir con @PreAuthorize.");
        }

        logger.info("Mostrando página de inventario con {} tareas.", tareas.size());
        return "inventario"; // Asegúrate de que este sea el nombre correcto de tu archivo HTML (ej.
                                 // src/main/resources/templates/gestion-tareas.html)
    }
}
