package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Para mapear la fecha_pago

/**
 * Entidad que representa un registro de pago en la tienda.
 */
@Entity
@Getter // Genera automáticamente los getters
@Setter // Genera automáticamente los setters
@NoArgsConstructor // Genera un constructor sin argumentos, requerido por JPA
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Pagos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago") // Mapea al nombre de columna en la BD
    private Integer idPago; // Identificador único del pago (int en la BD)

    @ManyToOne // Relación muchos a uno: muchos pagos pueden ser realizados por un usuario
    @JoinColumn(name = "usuario_id", nullable = false) // Columna de clave foránea en la tabla 'pagos'
    private Usuario usuario; // El usuario que realizó el pago

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal; // Monto total del pago

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago; // Método de pago utilizado (ej. "Tarjeta", "PayPal", "Efectivo")

    @Column(name = "estado_pago") // La BD tiene DEFAULT 'Completado', por lo que es nullable=true por defecto
    private String estadoPago; // Estado del pago (ej. "Completado", "Pendiente", "Fallido")

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago; // Fecha y hora en que se realizó el pago

    // Constructor personalizado si necesitas inicializar fechaPago manualmente,
    // de lo contrario, @AllArgsConstructor y @NoArgsConstructor son suficientes.
    // Si la base de datos maneja el DEFAULT CURRENT_TIMESTAMP(), no necesitas inicializarlo aquí
    // a menos que quieras anular ese comportamiento.
}
