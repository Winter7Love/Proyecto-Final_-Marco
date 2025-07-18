package com.example.demo.repository;

import com.example.demo.model.Carrito;
// import com.example.demo.model.Producto; // Importación eliminada: no usada directamente en firmas de método
// import com.example.demo.model.Usuario; // Importación eliminada: no usada directamente en firmas de método
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Carrito.
 * Proporciona métodos CRUD y consultas personalizadas para la tabla 'carrito'.
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> { // El ID de Carrito es Long, esto es correcto.

    /**
     * Busca todos los ítems del carrito asociados a un ID de usuario específico.
     * @param usuarioId El ID del usuario.
     * @return Una lista de objetos Carrito pertenecientes al usuario.
     */
    List<Carrito> findByUsuarioId(Integer usuarioId); // Cambiado a Integer para coincidir con el tipo de ID de Usuario

    /**
     * Busca un ítem del carrito específico por el ID del usuario y el ID del producto.
     * Esto es útil para verificar si un producto ya existe en el carrito de un usuario.
     * @param usuarioId El ID del usuario.
     * @param productoId El ID del producto.
     * @return El objeto Carrito si se encuentra, o null si no existe.
     */
    Carrito findByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId); // Cambiado a Integer para coincidir con los tipos de ID de Usuario y Producto

    // Puedes añadir más métodos personalizados si son necesarios para tu lógica de negocio.
    // Por ejemplo:
    // void deleteByUsuarioId(Integer usuarioId); // Para vaciar el carrito de un usuario
}
