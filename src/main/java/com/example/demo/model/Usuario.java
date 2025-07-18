package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id") 
    private Rol rol; 

    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Constructor completo para crear una instancia de Usuario.
     * @param id Identificador del usuario.
     * @param nombreUsuario Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña (ya encriptada).
     * @param nombreCompleto Nombre completo.
     * @param telefono Número de teléfono.
     * @param direccion Dirección.
     * @param rol Rol asignado al usuario.
     */
    public Usuario(Integer id, String nombreUsuario, String email, String password, String nombreCompleto, String telefono,
                   String direccion, Rol rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = rol;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    // --- Implementación de UserDetails (para Spring Security) ---

    /**
     * Devuelve los roles/autoridades concedidas al usuario.
     * Spring Security espera que los roles tengan el prefijo "ROLE_".
     * @return Colección de GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase()));
    }

    /**
     * Devuelve el nombre de usuario utilizado para la autenticación.
     * @return El nombre de usuario.
     */
    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     * @return true si la cuenta es válida (no ha expirado), false en caso contrario.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Por defecto, las cuentas no expiran. Implementar lógica si es necesario.
    }

    /**
     * Indica si la cuenta del usuario está bloqueada.
     * @return true si la cuenta no está bloqueada, false en caso contrario.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Por defecto, las cuentas no están bloqueadas. Implementar lógica si es necesario.
    }

    /**
     * Indica si las credenciales (contraseña) del usuario han expirado.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por defecto, las credenciales no expiran. Implementar lógica si es necesario.
    }

    /**
     * Indica si el usuario está habilitado o deshabilitado.
     * @return true si el usuario está habilitado, false en caso contrario.
     */
    @Override
    public boolean isEnabled() {
        return true; // Por defecto, los usuarios están habilitados. Implementar lógica si es necesario (ej. un campo 'estado' en Usuario).
    }
}
