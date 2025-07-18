package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate; // Importar RestTemplate

/**
 * Clase de configuración general de la aplicación para definir beans comunes.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Define el codificador de contraseñas que se utilizará en la aplicación.
     * BCryptPasswordEncoder es una opción robusta para encriptar contraseñas.
     * @return Una instancia de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define un bean para RestTemplate, que se utilizará para realizar
     * llamadas HTTP a APIs externas.
     * @return Una instancia de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
