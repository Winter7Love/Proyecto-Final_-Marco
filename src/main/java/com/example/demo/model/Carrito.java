package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column; // Importar para @Column si se usa explícitamente
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Añadir para el constructor vacío si no hay otros

/**
 * Entidad que representa un ítem en el carrito de compras de un usuario.
 */
@Entity
@Getter // Genera automáticamente los getters para todos los campos
@Setter // Genera automáticamente los setters para todos los campos (excepto setCantidad, que es manual)
@NoArgsConstructor // Genera un constructor sin argumentos, requerido por JPA
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único del ítem del carrito (bigint en la BD)

    @ManyToOne // Relación muchos a uno: muchos ítems de carrito para un usuario
    @JoinColumn(name = "usuario_id", nullable = false) // Columna de clave foránea en la tabla 'carrito'
    private Usuario usuario; // El usuario al que pertenece este carrito

    @ManyToOne // Relación muchos a uno: muchos ítems de carrito para un producto
    @JoinColumn(name = "producto_id", nullable = false) // Columna de clave foránea en la tabla 'carrito'
    private Producto producto; // El producto en el carrito

    @Column(name = "cantidad", nullable = false) // Mapeo explícito de la columna
    private int cantidad; // Cantidad del producto específico en el carrito

    /**
     * Setter personalizado para la cantidad.
     * Incluye una validación para asegurar que la cantidad sea mayor que cero.
     * @param cantidad La cantidad del producto a establecer.
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero.
     */
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.cantidad = cantidad;
    }

    // Nota: Lombok se encarga de generar los getters y setters para 'id', 'usuario' y 'producto'.
    // El setter para 'cantidad' es manual y anula el generado por Lombok.
}
