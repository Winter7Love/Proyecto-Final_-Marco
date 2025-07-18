package com.example.demo.repository;

import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Importar la anotación @Repository

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 * Proporciona métodos CRUD y consultas personalizadas para la tabla 'producto'.
 */
@Repository // Anotación para indicar que esta interfaz es un componente de repositorio de Spring
public interface ProductoRepository extends JpaRepository<Producto, Integer> { // Cambiado Long a Integer para coincidir con Producto.java

    /**
     * Busca productos por su categoría.
     * @param categoria La categoría de los productos a buscar.
     * @return Una lista de productos que pertenecen a la categoría especificada.
     */
    List<Producto> findByCategoria(String categoria);

    /**
     * Busca un producto por su identificador único.
     * @param id El ID del producto.
     * @return Un Optional que contiene el Producto si se encuentra, o vacío si no.
     */
    Optional<Producto> findById(Integer id); // Cambiado Long a Integer para coincidir con Producto.java

    // Puedes añadir más métodos personalizados si son necesarios para tu lógica de negocio.
    // Por ejemplo:
    // List<Producto> findByNombreContainingIgnoreCase(String nombre);
    // List<Producto> findByMarca(String marca);
}
