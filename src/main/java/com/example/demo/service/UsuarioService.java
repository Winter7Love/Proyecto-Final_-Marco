package com.example.demo.service;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.Optional;

/**
 * Servicio para la gestión de usuarios e implementación de UserDetailsService
 * para la autenticación con Spring Security.
 */
@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    /**
     * Constructor para inyectar las dependencias necesarias.
     * @param usuarioRepository Repositorio para operaciones CRUD de Usuario.
     * @param passwordEncoder Codificador de contraseñas.
     * @param rolRepository Repositorio para operaciones CRUD de Rol.
     */
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    // =================================================================
    // MÉTODOS PÚBLICOS DEL SERVICIO
    // =================================================================

    /**
     * Guarda un nuevo usuario en la base de datos, encriptando su contraseña
     * y asignándole un rol por defecto si no tiene uno.
     * @param usuario El objeto Usuario a guardar.
     */
    public void guardarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardar
        String contraseñaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contraseñaEncriptada);

        if (usuario.getRol() == null) {
            Optional<Rol> defaultRolOptional = rolRepository.findByNombreRol("MIEMBRO"); // Asegúrese de que este rol exista
            if (defaultRolOptional.isPresent()) {
                usuario.setRol(defaultRolOptional.get());
                logger.info("Asignado rol 'MIEMBRO' por defecto al usuario: {}", usuario.getNombreUsuario());
            } else {
                logger.warn("El rol 'MIEMBRO' no se encontró en la base de datos. El usuario se guardará sin rol.");
            }
        }

        usuarioRepository.save(usuario);
        logger.info("Usuario guardado exitosamente: {}", usuario.getNombreUsuario());
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email La dirección de correo electrónico del usuario.
     * @return Un Optional que contiene el Usuario si se encuentra, o vacío si no.
     */
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Valida si la contraseña proporcionada en texto plano coincide con la contraseña encriptada del usuario.
     * @param usuario El objeto Usuario cuya contraseña se va a validar.
     * @param rawPassword La contraseña en texto plano a verificar.
     * @return true si las contraseñas coinciden, false en caso contrario.
     */
    public boolean validarCredenciales(Usuario usuario, String rawPassword) {
        return passwordEncoder.matches(rawPassword, usuario.getPassword());
    }

    /**
     * Guarda/actualiza un usuario en la base de datos.
     * Usado para el reseteo de contraseña o actualizaciones de perfil.
     * @param usuario El objeto Usuario a actualizar.
     * @return El usuario actualizado y persistido.
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        Usuario savedUser = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente: {}", savedUser.getNombreUsuario());
        return savedUser;
    }

    // =================================================================
    // MÉTODO REQUERIDO POR SPRING SECURITY (UserDetailsService)
    // =================================================================

    /**
     * Carga los detalles de un usuario para que Spring Security lo gestione.
     * Este método es llamado automáticamente durante el proceso de login.
     * Intentará buscar el usuario por email primero, y si no lo encuentra, por nombre de usuario.
     * @param usernameOrEmail El nombre de usuario o email a buscar.
     * @return Un objeto UserDetails que representa al usuario autenticado.
     * @throws UsernameNotFoundException Si el usuario no es encontrado.
     */

@Override
@Transactional
public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    logger.info("Intentando cargar usuario con nombre de usuario o email: {}", usernameOrEmail);

    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(usernameOrEmail);

    if (usuarioOpt.isEmpty()) {
        usuarioOpt = usuarioRepository.findByNombreUsuario(usernameOrEmail);
    }

    Usuario usuario = usuarioOpt
            .orElseThrow(() -> {
                logger.warn("Usuario no encontrado con nombre de usuario o email: {}", usernameOrEmail);
                return new UsernameNotFoundException("Usuario no encontrado con: " + usernameOrEmail);
            });

    logger.info("Usuario encontrado: {} con rol: {}", usuario.getNombreUsuario(), usuario.getRol().getNombreRol());
    // ¡NUEVO LOG PARA DEPURACIÓN!
    logger.debug("Contraseña encriptada del usuario {}: {}", usuario.getNombreUsuario(), usuario.getPassword());

    return usuario;
}

}