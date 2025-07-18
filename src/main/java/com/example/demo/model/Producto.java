package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column; // Importar para @Column
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Para constructor vacío
import lombok.AllArgsConstructor; // Para constructor con todos los argumentos

/**
 * Entidad que representa un producto en la tienda.
 */
@Entity
@Getter // Genera automáticamente los getters
@Setter // Genera automáticamente los setters
@NoArgsConstructor // Genera un constructor sin argumentos, requerido por JPA
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Identificador único del producto. Cambiado de Long a Integer para coincidir con la BD (int(11)).

    @Column(name = "marca", nullable = false)
    private String marca; // Marca del producto

    @Column(name = "nombre", nullable = false)
    private String nombre; // Nombre del producto

    @Column(name = "precio", nullable = false)
    private Double precio; // Precio de venta del producto

    @Column(name = "precio_original", nullable = false)
    private Double precioOriginal; // Precio original del producto (antes de descuentos, etc.)

    @Column(name = "stock", nullable = false)
    private Integer stock; // Cantidad de stock disponible del producto

    @Column(name = "rating", nullable = false)
    private Integer rating; // Calificación o rating del producto

    @Column(name = "imagen", nullable = false)
    private String imagen; // Ruta o nombre del archivo de imagen del producto

    @Column(name = "categoria") // La BD permite nulos para esta columna
    private String categoria; // Categoría a la que pertenece el producto
}
