package com.example.demo.service;

import com.example.demo.model.Carrito;
import com.example.demo.repository.CarritoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión del carrito de compras de los usuarios.
 * Proporciona operaciones para añadir, eliminar, actualizar y vaciar productos del carrito.
 */
@Service
public class CarritoService {

    private static final Logger logger = LoggerFactory.getLogger(CarritoService.class);

    private final CarritoRepository carritoRepository;

    /**
     * Constructor para inyectar la dependencia de CarritoRepository.
     * @param carritoRepository Repositorio para operaciones CRUD de Carrito.
     */
    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    /**
     * Obtiene todos los productos del carrito asociados a un usuario específico.
     * @param usuarioId El ID del usuario.
     * @return Una lista de objetos Carrito pertenecientes al usuario. Retorna una lista vacía si el ID es nulo o no se encuentran productos.
     */
    public List<Carrito> obtenerCarritoPorUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            logger.warn("El usuarioId es null, no se puede obtener el carrito.");
            return new ArrayList<>(); // Retorna un carrito vacío
        }

        List<Carrito> carrito = carritoRepository.findByUsuarioId(usuarioId);
        logger.info("Carrito obtenido para el usuario ID {}: {} productos.", usuarioId, carrito.size());
        return carrito;
    }

    /**
     * Agrega un producto al carrito de compras de un usuario.
     * Si el producto ya existe en el carrito, actualiza su cantidad. De lo contrario, añade un nuevo ítem.
     * @param carrito El objeto Carrito que contiene el usuario, producto y cantidad a agregar.
     */
    public void agregarProductoAlCarrito(Carrito carrito) {
        if (carrito == null || carrito.getUsuario() == null || carrito.getProducto() == null) {
            logger.error("Intento de agregar un carrito nulo o con usuario/producto nulo.");
            throw new IllegalArgumentException("El objeto carrito, usuario o producto no pueden ser nulos.");
        }

        Integer usuarioId = carrito.getUsuario().getId();
        Integer productoId = carrito.getProducto().getId();

        logger.info("Intentando agregar el producto '{}' (ID: {}) al carrito del usuario (ID: {}).",
                    carrito.getProducto().getNombre(), productoId, usuarioId);

        Carrito existingCarrito = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (existingCarrito != null) {
            existingCarrito.setCantidad(existingCarrito.getCantidad() + carrito.getCantidad());
            carritoRepository.save(existingCarrito);
            logger.info("Producto actualizado en el carrito: '{}' con nueva cantidad: {}.",
                        existingCarrito.getProducto().getNombre(), existingCarrito.getCantidad());
        } else {
            carritoRepository.save(carrito);
            logger.info("Nuevo producto agregado al carrito: '{}'.", carrito.getProducto().getNombre());
        }
    }

    /**
     * Elimina un producto específico del carrito de compras de un usuario.
     * @param productoId El ID del producto a eliminar.
     * @param usuarioId El ID del usuario cuyo carrito se va a modificar.
     */
    public void eliminarProductoDelCarrito(Integer productoId, Integer usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (carrito != null) {
            carritoRepository.delete(carrito);
            logger.info("Producto (ID: {}) eliminado del carrito del usuario (ID: {}).", productoId, usuarioId);
        } else {
            logger.warn("No se encontró el producto (ID: {}) en el carrito del usuario (ID: {}).", productoId, usuarioId);
        }
    }

    /**
     * Vacía completamente el carrito de un usuario.
     * @param usuarioId El ID del usuario cuyo carrito se va a vaciar.
     */
    public void vaciarCarrito(Integer usuarioId) {
        if (usuarioId == null) {
            logger.warn("El usuarioId es null, no se puede vaciar el carrito.");
            return;
        }
        List<Carrito> productosCarrito = carritoRepository.findByUsuarioId(usuarioId);
        if (!productosCarrito.isEmpty()) {
            carritoRepository.deleteAll(productosCarrito);
            logger.info("Carrito vaciado para el usuario ID {}.", usuarioId);
        } else {
            logger.info("El carrito del usuario ID {} ya estaba vacío.", usuarioId);
        }
    }

    /**
     * Actualiza la cantidad de un producto específico en el carrito de un usuario.
     * Si la cantidad resultante es 0 o menos, el producto se elimina del carrito.
     * @param productoId El ID del producto a actualizar.
     * @param usuarioId El ID del usuario cuyo carrito se va a modificar.
     * @param cantidad La nueva cantidad del producto.
     * @return El objeto Carrito actualizado si se encuentra, o null si no se encuentra el ítem.
     * @throws IllegalArgumentException si la cantidad es inválida (ej. negativa, si no se elimina).
     */
    public Carrito actualizarCantidad(Integer productoId, Integer usuarioId, int cantidad) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (carrito != null) {
            if (cantidad <= 0) {
                // Si la cantidad es 0 o menos, eliminamos el producto del carrito
                eliminarProductoDelCarrito(productoId, usuarioId);
                logger.info("Producto (ID: {}) eliminado del carrito del usuario (ID: {}) porque la cantidad es {}.",
                            productoId, usuarioId, cantidad);
                return null; // Retorna null porque el ítem ha sido eliminado
            } else {
                // Actualiza la cantidad (la validación de cantidad > 0 ya está en el setter del modelo)
                carrito.setCantidad(cantidad);
                Carrito updatedCarrito = carritoRepository.save(carrito);
                logger.info("Cantidad del producto (ID: {}) en el carrito del usuario (ID: {}) actualizada a {}.",
                            productoId, usuarioId, cantidad);
                return updatedCarrito;
            }
        } else {
            logger.warn("No se encontró el producto (ID: {}) en el carrito del usuario (ID: {}) para actualizar la cantidad.",
                        productoId, usuarioId);
            // Podrías lanzar una EntityNotFoundException aquí si prefieres un manejo de errores más estricto.
            return null;
        }
    }
}
