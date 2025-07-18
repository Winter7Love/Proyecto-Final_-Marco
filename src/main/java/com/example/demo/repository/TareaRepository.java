package com.example.demo.repository;

import com.example.demo.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Importar la anotación @Repository

/**
 * Repositorio para la entidad Tarea.
 * Proporciona métodos CRUD básicos para la tabla 'tarea'.
 * Puedes agregar métodos personalizados aquí si es necesario,
 * por ejemplo, para buscar tareas por título, prioridad, etc.
 */
@Repository // Anotación para indicar que esta interfaz es un componente de repositorio de Spring
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // Ejemplo de método personalizado:
    // List<Tarea> findByPrioridad(String prioridad);
    // List<Tarea> findByTituloContainingIgnoreCase(String titulo);
}
