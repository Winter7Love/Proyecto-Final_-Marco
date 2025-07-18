package com.example.demo.security.jwt;

import com.example.demo.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher; // Importar AntPathMatcher
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro para procesar la autenticación JWT para cada solicitud entrante.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    // Define las rutas que este filtro debe IGNORAR
    private static final List<String> EXCLUDE_URL_PATTERNS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register"
            // Puedes añadir más rutas públicas aquí si es necesario
    );

    private AntPathMatcher pathMatcher = new AntPathMatcher(); // Para comparar patrones de URL

    public AuthTokenFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Log para depuración: Ruta de la solicitud
        logger.debug("AuthTokenFilter procesando solicitud para la URI: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = usuarioService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Usuario {} autenticado con JWT.", username);
            } else if (jwt == null) {
                logger.debug("No se encontró JWT en el encabezado de autorización.");
            } else {
                logger.warn("JWT inválido o expirado.");
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario (JWT): {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    // Sobrescribe este método para que el filtro no se ejecute en las rutas excluidas
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();
        // Usar pathMatcher para comparar la URI con los patrones excluidos
        boolean shouldExclude = EXCLUDE_URL_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));

        if (shouldExclude) {
            logger.debug("AuthTokenFilter saltando para la URI: {}", requestUri);
        }
        return shouldExclude;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
