package com.example.demo.service;

import com.example.demo.model.Producto;
import com.example.demo.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio para la gestión de productos en la tienda.
 * Proporciona operaciones para listar, obtener y buscar productos.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Constructor para inyectar la dependencia de ProductoRepository.
     * @param productoRepository Repositorio para operaciones CRUD de Producto.
     */
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Obtiene una lista de todos los productos disponibles en la base de datos.
     * @return Una lista de objetos Producto.
     */
    public List<Producto> listarProductos() {
        return productoRepository.findAll(); // Obtiene todos los productos de la base de datos
    }

    /**
     * Obtiene un producto específico por su identificador único.
     * @param id El ID del producto a buscar (debe ser Integer).
     * @return El objeto Producto si se encuentra.
     * @throws EntityNotFoundException Si no se encuentra ningún producto con el ID proporcionado.
     */
    public Producto obtenerProductoPorId(Integer id) { // Cambiado de Long a Integer
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto con ID " + id + " no encontrado"));
    }

    /**
     * Obtiene una lista de productos que pertenecen a una categoría específica.
     * @param categoria La categoría de los productos a buscar.
     * @return Una lista de productos que coinciden con la categoría.
     */
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        // Busca todos los productos que coinciden con la categoría
        return productoRepository.findByCategoria(categoria);
    }

    // Puedes añadir métodos para guardar, actualizar o eliminar productos si es necesario.
    // public Producto guardarProducto(Producto producto) {
    //     return productoRepository.save(producto);
    // }
    //
    // public void eliminarProducto(Integer id) {
    //     productoRepository.deleteById(id);
    // }
}
