package com.example.demo.repository;

import com.example.demo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Busca un rol por su nombre (ej. "admin", "miembro")
    Optional<Rol> findByNombreRol(String nombreRol);
}