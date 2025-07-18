package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para proporcionar la tasa de cambio de PEN a USD
 * obteniéndola de una API externa (ExchangeRate-API).
 */
@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

    // Inyecta la clave de API desde application.properties
    @Value("${exchangerateapi.api-key}")
    private String apiKey;

    // Inyecta la URL base de la API externa desde application.properties
    @Value("${exchangerateapi.base-url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate;

    public ExchangeRateController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Devuelve la tasa de cambio actual de PEN a USD obtenida de ExchangeRate-API.
     * La API devuelve la tasa de 1 USD a X PEN.
     *
     * @return Un mapa que contiene la tasa de cambio.
     * @throws IllegalStateException Si la clave de API no está configurada,
     * si la respuesta de la API es inválida,
     * o si la tasa de conversión no es positiva.
     * @throws RuntimeException      Si ocurre un error de red o HTTP al comunicarse con la API externa.
     */
    @GetMapping("/exchange-rate")
    public Map<String, Double> getExchangeRate() {
        Map<String, Double> response = new HashMap<>();

        // Validación de la clave de API: Si no está configurada o es el valor por defecto,
        // lanzamos una excepción inmediatamente, ya que el usuario no quiere un fallback estático.
        if (apiKey == null || apiKey.isEmpty() || "YOUR_EXCHANGERATE_API_KEY_HERE".equals(apiKey)) {
            logger.error("API Key para ExchangeRate-API no configurada o es el valor por defecto. No se puede obtener la tasa de cambio.");
            throw new IllegalStateException("API Key para ExchangeRate-API no configurada o es el valor por defecto.");
        }

        // Construcción correcta de la URL de la API externa usando la base y la clave
        // Ejemplo: https://v6.exchangerate-api.com/v6/TU_API_KEY/pair/USD/PEN
        String apiUrl = String.format("%s%s/pair/USD/PEN", apiBaseUrl, apiKey);
        logger.info("Intentando obtener tasa de cambio de la API externa: {}", apiUrl);

        try {
            // Realiza la llamada HTTP GET a la API externa
            ResponseEntity<Map<String, Object>> apiResponseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null, // No hay cuerpo de solicitud para GET
                new ParameterizedTypeReference<Map<String, Object>>() {} // Define el tipo esperado de la respuesta JSON
            );

            Map<String, Object> apiResponse = apiResponseEntity.getBody();

            // Verifica si la respuesta es exitosa y contiene la tasa de conversión
            if (apiResponse != null && "success".equals(apiResponse.get("result"))) {
                Object conversionRateObj = apiResponse.get("conversion_rate");

                // Asegura que el valor de la tasa de conversión es un número y es positivo
                if (conversionRateObj instanceof Number) {
                    Double conversionRate = ((Number) conversionRateObj).doubleValue();
                    if (conversionRate != null && conversionRate > 0) {
                        response.put("rate", conversionRate);
                        logger.info("Tasa de cambio obtenida exitosamente de la API: 1 USD = {} PEN", conversionRate);
                        return response;
                    } else {
                        // La tasa de conversión es 0, nula o negativa, lo cual es inválido
                        logger.error("La API de tasa de cambio devolvió una tasa de conversión no positiva o nula: {}", conversionRate);
                        throw new IllegalStateException("La API de tasa de cambio devolvió una tasa de conversión inválida.");
                    }
                }
            }
            // Si la respuesta no es "success" o no contiene "conversion_rate" en el formato esperado
            logger.error("Respuesta de la API de tasa de cambio exitosa pero sin 'conversion_rate' o formato inesperado: {}", apiResponse);
            throw new IllegalStateException("Formato de respuesta inesperado de la API de tasa de cambio.");

        } catch (HttpClientErrorException e) {
            // Manejo de errores HTTP (ej. 400 Bad Request, 404 Not Found, 401 Unauthorized)
            logger.error("Error HTTP al llamar a la API de tasa de cambio: Código {} - Cuerpo de respuesta: {}. Mensaje: {}", e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
            throw new RuntimeException("Error al comunicarse con la API de tasa de cambio (HTTP Error).", e);
        } catch (ResourceAccessException e) {
            // Manejo de errores de conexión (ej. host no alcanzable, timeout)
            logger.error("Error de conexión al llamar a la API de tasa de cambio: {}. Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
            throw new RuntimeException("Error de red al comunicarse con la API de tasa de cambio.", e);
        } catch (Exception e) {
            // Manejo de cualquier otra excepción inesperada
            logger.error("Error inesperado al obtener la tasa de cambio: {}. Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
            throw new RuntimeException("Error inesperado al obtener la tasa de cambio.", e);
        }
    }
}
