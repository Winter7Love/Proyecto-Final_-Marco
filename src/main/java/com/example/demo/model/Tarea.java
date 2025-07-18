package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalDate; // Correctly import LocalDate
// import java.time.LocalDateTime; // No longer needed if 'fecha' is LocalDate

/**
 * Entidad que representa una tarea o un elemento de inventario con
 * características específicas.
 * Nota: El nombre de la tabla en la base de datos es 'tarea'.
 */
@Entity
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único de la tarea, mapeado a bigint(20) en SQL

    @Column(name = "titulo") // Mapeo explícito para claridad
    private String titulo; // Título o nombre de la tarea/producto

    @Column(name = "descripcion") // Mapeo explícito
    private String descripcion; // Descripción detallada de la tarea/producto

    @Column(name = "fecha") // Mapeo explícito
    private LocalDate fecha; // ¡MODIFICADO! Tipo de dato a LocalDate para coincidir con el input HTML y la
                             // BD.

    @Column(name = "prioridad") // Mapeo explícito
    private String prioridad; // Prioridad de la tarea (ej. "Alta", "Media", "Baja")

    @Column(name = "stock") // Mapeo explícito
    private int stock; // Cantidad en stock (aunque el nombre 'tarea' es inusual para stock, se
                       // mantiene por el esquema)

    // Constructor vacío requerido por JPA
    public Tarea() {
    }

    /**
     * Constructor completo para crear una instancia de Tarea.
     * 
     * @param id          Identificador de la tarea.
     * @param titulo      Título de la tarea.
     * @param descripcion Descripción de la tarea.
     * @param fecha       Fecha y hora de la tarea.
     * @param prioridad   Prioridad de la tarea.
     * @param stock       Cantidad en stock.
     */
    public Tarea(Long id, String titulo, String descripcion, LocalDate fecha, String prioridad, int stock) { // ¡MODIFICADO!
                                                                                                             // El
                                                                                                             // parámetro
                                                                                                             // 'fecha'
                                                                                                             // es
                                                                                                             // LocalDate
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.stock = stock;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() { // ¡MODIFICADO! El retorno es LocalDate
        return fecha;
    }

    public void setFecha(LocalDate fecha) { // ¡MODIFICADO! El parámetro es LocalDate
        this.fecha = fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}