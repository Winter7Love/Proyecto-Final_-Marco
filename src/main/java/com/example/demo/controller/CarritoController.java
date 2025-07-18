package com.example.demo.controller;

import com.example.demo.model.Carrito;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.service.CarritoService;
import com.example.demo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con el carrito de compras.
 * Proporciona endpoints para ver, agregar, eliminar, vaciar y actualizar productos en el carrito.
 * Requiere autenticación para todas sus operaciones.
 */
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    private final CarritoService carritoService;
    private final ProductoService productoService;

    /**
     * Constructor para inyectar las dependencias de CarritoService y ProductoService.
     * @param carritoService Servicio para la lógica de negocio del carrito.
     * @param productoService Servicio para la lógica de negocio de productos.
     */
    public CarritoController(CarritoService carritoService, ProductoService productoService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
    }

    /**
     * Muestra la página del carrito de compras del usuario autenticado.
     * @param usuario El objeto Usuario autenticado, inyectado por Spring Security.
     * @param model El modelo de Spring MVC para pasar datos a la vista.
     * @return El nombre de la vista "carrito".
     */
    @GetMapping("/ver")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden ver el carrito
    public String verCarrito(@AuthenticationPrincipal Usuario usuario, Model model) {
        List<Carrito> carrito = carritoService.obtenerCarritoPorUsuario(usuario.getId());
        double totalGeneral = carrito.stream()
                .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("totalGeneral", totalGeneral); // Pasa el total al modelo
        model.addAttribute("usuario", usuario); // Pasar el usuario para la navbar
        logger.info("Mostrando carrito para el usuario ID {}. Total de productos: {}. Total general: {}",
                    usuario.getId(), carrito.size(), totalGeneral);
        return "carrito"; // Asegúrate de que "carrito.html" esté en tus templates
    }

    /**
     * Agrega un producto al carrito de compras de un usuario.
     * Si el producto ya existe en el carrito, actualiza su cantidad. De lo contrario, añade un nuevo ítem.
     * @param productoId El ID del producto a agregar.
     * @param usuario El objeto Usuario autenticado.
     * @return Redirecciona a la página del carrito con un mensaje de éxito o error.
     */
    @PostMapping("/agregar")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden agregar productos al carrito
    public String agregarProductoAlCarrito(@RequestParam("productoId") Integer productoId,
                                           @AuthenticationPrincipal Usuario usuario) {
        try {
            Producto producto = productoService.obtenerProductoPorId(productoId);

            Carrito carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito.setProducto(producto);
            carrito.setCantidad(1); // Por defecto, se agrega una unidad

            carritoService.agregarProductoAlCarrito(carrito);
            logger.info("Producto ID {} agregado al carrito del usuario ID {}", productoId, usuario.getId());
            return "redirect:/carrito/ver?mensaje=Producto+agregado+correctamente";
        } catch (EntityNotFoundException e) {
            logger.error("Error al agregar producto al carrito: Producto con ID {} no encontrado. Mensaje: {}", productoId, e.getMessage());
            return "redirect:/carrito/ver?error=Producto+no+encontrado";
        } catch (IllegalArgumentException e) {
            logger.error("Error de argumento al agregar producto al carrito para usuario ID {}. Mensaje: {}", usuario.getId(), e.getMessage());
            return "redirect:/carrito/ver?error=Cantidad+inválida+o+datos+faltantes";
        } catch (Exception e) {
            logger.error("Error inesperado al agregar producto ID {} al carrito del usuario ID {}. Mensaje: {}", productoId, usuario.getId(), e.getMessage(), e);
            return "redirect:/carrito/ver?error=Hubo+un+problema+al+agregar+el+producto";
        }
    }

    /**
     * Elimina un producto específico del carrito de compras del usuario autenticado.
     * @param productoId El ID del producto a eliminar.
     * @param usuario El objeto Usuario autenticado.
     * @return Redirecciona a la página del carrito.
     */
    @PostMapping("/eliminar")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden eliminar productos del carrito
    public String eliminarProductoDelCarrito(@RequestParam("productoId") Integer productoId,
                                           @AuthenticationPrincipal Usuario usuario) {
        carritoService.eliminarProductoDelCarrito(productoId, usuario.getId());
        logger.info("Producto ID {} eliminado del carrito del usuario ID {}", productoId, usuario.getId());
        return "redirect:/carrito/ver?mensaje=Producto+eliminado+correctamente"; // Mensaje de éxito
    }

    /**
     * Vacía completamente el carrito de compras del usuario.
     * @param usuario El objeto Usuario autenticado.
     * @return Redirecciona a la página del carrito.
     */
    @PostMapping("/vaciar")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden vaciar el carrito
    public String vaciarCarrito(@AuthenticationPrincipal Usuario usuario) {
        carritoService.vaciarCarrito(usuario.getId());
        logger.info("Carrito vaciado para el usuario ID {}", usuario.getId());
        return "redirect:/carrito/ver?mensaje=Carrito+vaciado+correctamente"; // Mensaje de éxito
    }

    /**
     * Actualiza la cantidad de un producto específico en el carrito del usuario.
     * Este método es llamado por los botones +/- en el HTML.
     * @param productoId El ID del producto a actualizar.
     * @param cantidad La nueva cantidad del producto.
     * @param usuario El objeto Usuario autenticado.
     * @return Redirecciona a la página del carrito con un mensaje de éxito o error.
     */
    @PostMapping("/actualizar")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados pueden actualizar la cantidad en el carrito
    public String actualizarCantidad(@RequestParam("productoId") Integer productoId,
                                     @RequestParam("cantidad") int cantidad,
                                     @AuthenticationPrincipal Usuario usuario) {
        try {
            // El servicio CarritoService.actualizarCantidad ya maneja la eliminación si cantidad <= 0
            carritoService.actualizarCantidad(productoId, usuario.getId(), cantidad);
            logger.info("Cantidad del producto ID {} actualizada a {} para el usuario ID {}.",
                        productoId, cantidad, usuario.getId());
            return "redirect:/carrito/ver?mensaje=Cantidad+actualizada+correctamente";
        } catch (IllegalArgumentException e) {
            logger.error("Error al actualizar cantidad para producto ID {} en carrito de usuario ID {}: {}", productoId, usuario.getId(), e.getMessage());
            return "redirect:/carrito/ver?error=" + e.getMessage().replace(" ", "+"); // Pasa el mensaje de error
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar cantidad para producto ID {} en carrito de usuario ID {}: {}", productoId, usuario.getId(), e.getMessage(), e);
            return "redirect:/carrito/ver?error=Error+inesperado+al+actualizar+cantidad";
        }
    }
}
