package com.example.demo.repository;

import com.example.demo.model.Pagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Pagos.
 * Proporciona métodos CRUD básicos para la tabla 'pagos'.
 * Puedes añadir métodos personalizados aquí si es necesario,
 * por ejemplo, para buscar pagos por usuario, por fecha, etc.
 */
@Repository // Anotación para indicar que esta interfaz es un componente de repositorio de Spring
public interface PagosRepository extends JpaRepository<Pagos, Integer> {
    // Ejemplo de método personalizado:
    // List<Pagos> findByUsuarioId(Integer usuarioId);
    // List<Pagos> findByFechaPagoBetween(LocalDateTime startDate, LocalDateTime endDate);
}
