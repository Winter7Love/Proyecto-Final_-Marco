package com.example.demo.config;

import com.example.demo.security.jwt.AuthEntryPointJwt;
import com.example.demo.security.jwt.AuthTokenFilter;
import com.example.demo.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Mantener esta importación

/**
 * Clase de configuración de seguridad para la aplicación Spring Boot.
 * Habilita la seguridad web y la seguridad a nivel de método
 * (para @PreAuthorize).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UsuarioService usuarioService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UsuarioService usuarioService,
                             AuthEntryPointJwt unauthorizedHandler,
                             AuthTokenFilter authTokenFilter,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authTokenFilter = authTokenFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define las reglas de autorización, y la configuración de JWT.
     *
     * @param http El objeto HttpSecurity para configurar la seguridad.
     * @return Una instancia de SecurityFilterChain.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas de autenticación API (JWT) - deben ser las primeras y permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Rutas públicas generales (CSS, JS, imágenes, home, login/register de formulario)
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/inmy-store",
                                "/css/**",
                                "/js/**",
                                "/imagenes/**",
                                "/Img-Perifericos/**",
                                "/IMG-MARCAS/**",
                                "/vendor/**",
                                "/api/exchange-rate").permitAll()

                        // 3. Rutas específicas para roles
                        .requestMatchers("/inventario/**", "/admin/**").hasRole("ADMIN")

                        // 4. Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configuración de formLogin (para la UI web)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                // Configuración de logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        // Configurar el proveedor de autenticación para HttpSecurity
        http.authenticationProvider(authenticationProvider());

        // ¡CAMBIO CLAVE AQUÍ!
        // Añadir el filtro JWT *después* del filtro de autenticación de usuario/contraseña estándar.
        // Esto asegura que las rutas permitAll() para login/register no sean interceptadas
        // por el filtro JWT antes de tiempo.
        http.addFilterAfter(authTokenFilter, UsernamePasswordAuthenticationFilter.class); // Cambiado a addFilterAfter

        return http.build();
    }
}
