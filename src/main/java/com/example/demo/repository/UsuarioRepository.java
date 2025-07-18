package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos CRUD y consultas personalizadas para la tabla 'usuario'.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { // El ID de Usuario es Integer

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email La dirección de correo electrónico del usuario.
     * @return Un Optional que contiene el Usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     * @param nombreUsuario El nombre de usuario.
     * @return Un Optional que contiene el Usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si existe un usuario con el nombre de usuario proporcionado.
     * @param nombreUsuario El nombre de usuario a verificar.
     * @return true si existe un usuario con ese nombre de usuario, false en caso contrario.
     */
    Boolean existsByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si existe un usuario con el correo electrónico proporcionado.
     * @param email El correo electrónico a verificar.
     * @return true si existe un usuario con ese correo electrónico, false en caso contrario.
     */
    Boolean existsByEmail(String email);
}