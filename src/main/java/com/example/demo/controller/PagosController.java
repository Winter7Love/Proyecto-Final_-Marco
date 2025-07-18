package com.example.demo.controller;

import com.example.demo.model.Carrito;
import com.example.demo.model.Pagos;
import com.example.demo.model.Usuario;
import com.example.demo.service.CarritoService;
import com.example.demo.service.PagosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador que gestiona las operaciones relacionadas con los pagos.
 * Proporciona endpoints para iniciar y procesar pagos del carrito de compras.
 */
@Controller
@RequestMapping("/pagos")
public class PagosController {

    private static final Logger logger = LoggerFactory.getLogger(PagosController.class);

    private final PagosService pagosService;
    private final CarritoService carritoService; // Necesario para obtener y vaciar el carrito

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param pagosService   Servicio para la lógica de negocio de pagos.
     * @param carritoService Servicio para la lógica de negocio del carrito.
     */
    public PagosController(PagosService pagosService, CarritoService carritoService) {
        this.pagosService = pagosService;
        this.carritoService = carritoService;
    }

    /**
     * Muestra la página de confirmación de pago, cargando los ítems del carrito.
     * Requiere que el usuario esté autenticado.
     *
     * @param usuario El objeto Usuario autenticado.
     * @param model   El modelo de Spring MVC.
     * @return El nombre de la vista "confirmar_pago".
     */
    @GetMapping("/confirmar")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden acceder a la confirmación de pago
    public String mostrarConfirmacionPago(@AuthenticationPrincipal Usuario usuario, Model model) {
        List<Carrito> carritoItems = carritoService.obtenerCarritoPorUsuario(usuario.getId());

        if (carritoItems.isEmpty()) {
            logger.warn("Usuario ID {} intentó confirmar pago con un carrito vacío.", usuario.getId());
            return "redirect:/carrito/ver?error=Carrito+vacío.+Agrega+productos+antes+de+pagar.";
        }

        double totalGeneral = carritoItems.stream()
                .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                .sum();

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("usuario", usuario); // Pasar el usuario para mostrar detalles si es necesario
        logger.info("Mostrando página de confirmación de pago para el usuario ID {}. Total a pagar: {}.",
                usuario.getId(), totalGeneral);
        return "confirmar_pago"; // Asume una vista Thymeleaf para la confirmación del pago
    }

    /**
     * Procesa el pago de PayPal, crea un registro de pago y vacía el carrito del
     * usuario.
     * Este endpoint es llamado por el JavaScript del SDK de PayPal.
     *
     * @param metodoPago         El método de pago (ej. "PayPal").
     * @param orderID            El ID de la orden de PayPal.
     * @param usuario            El objeto Usuario autenticado.
     * @param redirectAttributes Para añadir mensajes flash después de la
     *                           redirección.
     * @return Redirecciona a la página del carrito con un mensaje de éxito o error.
     */
    @PostMapping("/procesar") // Este es el método para PayPal
    @PreAuthorize("isAuthenticated()")
    public String procesarPago(@RequestParam("metodoPago") String metodoPago,
            @RequestParam("orderID") String orderID, // orderID de PayPal
            @AuthenticationPrincipal Usuario usuario,
            RedirectAttributes redirectAttributes) {
        logger.info("Procesando pago con {}. OrderID: {}. Usuario ID: {}", metodoPago, orderID, usuario.getId());
        try {
            List<Carrito> carritoItems = carritoService.obtenerCarritoPorUsuario(usuario.getId());

            if (carritoItems.isEmpty()) {
                logger.warn("Intento de procesar pago con carrito vacío para usuario ID {}.", usuario.getId());
                redirectAttributes.addFlashAttribute("error", "No hay productos en el carrito para procesar el pago.");
                return "redirect:/carrito/ver";
            }

            double totalGeneral = carritoItems.stream()
                    .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                    .sum();

            // Crear el objeto Pagos
            Pagos nuevoPago = new Pagos();
            nuevoPago.setUsuario(usuario);
            nuevoPago.setMontoTotal(totalGeneral);
            nuevoPago.setMetodoPago(metodoPago);
            nuevoPago.setEstadoPago("Completado"); // Asumimos que si llegamos aquí, PayPal ya capturó el pago
            nuevoPago.setFechaPago(LocalDateTime.now());

            // Guardar el pago en la base de datos
            Pagos savedPago = pagosService.guardarPago(nuevoPago);
            logger.info("Pago registrado exitosamente en la BD. ID de Pago: {}. Monto: {}. Método: {}",
                    savedPago.getIdPago(), savedPago.getMontoTotal(), savedPago.getMetodoPago());

            // Vaciar el carrito después de un pago exitoso
            carritoService.vaciarCarrito(usuario.getId());
            logger.info("Carrito vaciado para el usuario ID {} después del pago.", usuario.getId());

            // Redirige a la página del carrito con un mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Pago con PayPal completado! Tu orden está siendo procesada.");
            return "redirect:/carrito/ver";
        } catch (Exception e) {
            logger.error("Error al procesar el pago con {}. OrderID: {}. Usuario ID: {}. Mensaje: {}", metodoPago,
                    orderID, usuario.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Hubo un problema al procesar su pago con PayPal.");
            return "redirect:/carrito/ver";
        }
    }

    /**
     * Procesa el pago de Yape.
     * Este endpoint es llamado por el formulario del botón de Yape.
     *
     * @param montoTotal         El monto total a pagar, enviado desde el frontend.
     * @param usuario            El objeto Usuario autenticado.
     * @param redirectAttributes Para añadir mensajes flash después de la
     *                           redirección.
     * @return Redirecciona a la página del carrito con un mensaje de éxito o error.
     */
    @PostMapping("/procesarYape") // Este es el método para Yape
    @PreAuthorize("isAuthenticated()")
    public String procesarYape(@RequestParam("montoTotal") Double montoTotal,
            @AuthenticationPrincipal Usuario usuario,
            RedirectAttributes redirectAttributes) {
        logger.info("Procesando solicitud de pago con Yape. Monto: {}. Usuario ID: {}", montoTotal, usuario.getId());
        try {
            List<Carrito> carritoItems = carritoService.obtenerCarritoPorUsuario(usuario.getId());

            if (carritoItems.isEmpty()) {
                logger.warn("Intento de procesar pago Yape con carrito vacío para usuario ID {}.", usuario.getId());
                redirectAttributes.addFlashAttribute("error",
                        "No hay productos en el carrito para procesar el pago con Yape.");
                return "redirect:/carrito/ver";
            }

            // Aquí iría la lógica real de integración con la API de Yape para comerciantes
            // si la tuvieras. Para P2P, la verificación es manual.
            // Por ahora, simulamos un pago "confirmado" por el usuario.
            boolean yapePaymentConfirmedByUser = true; // Asume que el usuario hizo clic en "Confirmar Pago"

            if (yapePaymentConfirmedByUser) {
                // Crear el objeto Pagos
                Pagos nuevoPago = new Pagos();
                nuevoPago.setUsuario(usuario);
                nuevoPago.setMontoTotal(montoTotal);
                nuevoPago.setMetodoPago("Yape");
                nuevoPago.setEstadoPago("Pendiente de Verificación Manual"); // <--- CAMBIADO A "Pendiente de
                                                                             // Verificación Manual"
                nuevoPago.setFechaPago(LocalDateTime.now());

                Pagos savedPago = pagosService.guardarPago(nuevoPago);
                logger.info("Pago Yape registrado como 'Pendiente de Verificación Manual'. ID de Pago: {}. Monto: {}",
                        savedPago.getIdPago(), savedPago.getMontoTotal());

                // Vaciar el carrito después de la confirmación (o podrías hacerlo después de la
                // verificación manual)
                carritoService.vaciarCarrito(usuario.getId());
                logger.info("Carrito vaciado para el usuario ID {} después de la confirmación de pago Yape.",
                        usuario.getId());

                // Redirige a la página del carrito con un mensaje de éxito
                redirectAttributes.addFlashAttribute("mensaje",
                        "¡Pago con Yape confirmado! Tu orden está pendiente de verificación manual.");
                return "redirect:/carrito/ver";
            } else {
                logger.error("Simulación de pago Yape fallida para usuario ID {}.", usuario.getId());
                redirectAttributes.addFlashAttribute("error",
                        "El pago con Yape no pudo ser confirmado. Intenta de nuevo.");
                return "redirect:/carrito/ver";
            }

        } catch (Exception e) {
            logger.error("Error al procesar el pago con Yape para usuario ID {}. Mensaje: {}", usuario.getId(),
                    e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Hubo un problema inesperado al procesar el pago con Yape.");
            return "redirect:/carrito/ver";
        }
    }

    // --- Métodos @GetMapping("/exito") y @GetMapping("/error") COMENTADOS ---
    // Ya no son necesarios con la nueva lógica de redirección a /carrito/ver con
    // mensajes flash.
    /*
     * @GetMapping("/exito")
     * 
     * @PreAuthorize("isAuthenticated()")
     * public String pagoExito(@RequestParam("idPago") Integer idPago, Model model)
     * {
     * try {
     * Pagos pago = pagosService.obtenerPagoPorId(idPago);
     * model.addAttribute("pago", pago);
     * logger.info("Mostrando página de éxito para el pago ID {}.", idPago);
     * return "pago_exito";
     * } catch (EntityNotFoundException e) {
     * logger.
     * error("Error: Pago con ID {} no encontrado para la página de éxito. Mensaje: {}"
     * , idPago,
     * e.getMessage());
     * return "redirect:/pagos/error?mensaje=Detalles+de+pago+no+encontrados";
     * }
     * }
     * 
     * @GetMapping("/error")
     * 
     * @PreAuthorize("isAuthenticated()")
     * public String pagoError(@RequestParam(value = "mensaje", required = false)
     * String mensaje, Model model) {
     * model.addAttribute("mensajeError",
     * mensaje != null ? mensaje :
     * "Ocurrió un error inesperado al procesar su pago.");
     * logger.warn("Mostrando página de error de pago. Mensaje: {}", mensaje);
     * return "pago_error";
     * }
     */
}
