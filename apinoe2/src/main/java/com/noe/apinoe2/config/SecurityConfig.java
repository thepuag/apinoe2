package com.noe.apinoe2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad
 * Por ahora básica, se puede expandir para OAuth2/JWT
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;
    
    @Value("${app.cors.allowed-headers:*}")
    private String[] allowedHeaders;
    
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())
            
            // Configuración de CORS usando nuestro CorsConfigurationSource
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configuración de sesiones (stateless para APIs)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorización de endpoints
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a ciertos endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints que requieren autenticación
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                //.requestMatchers("/api/usuarios/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/productos/**").hasAnyRole("USER", "ADMIN")
                
                // Por ahora, permitir todo (cambiar en producción)
                .anyRequest().permitAll()
            )
            
            // Headers de seguridad
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentType -> {})
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configurar orígenes permitidos desde application.properties
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        
        // Configurar métodos permitidos
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // Configurar headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        
        // Permitir credenciales
        configuration.setAllowCredentials(allowCredentials);
        
        // Exponer headers específicos si es necesario
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //TODO: Agregar más adelante para OAuth2/JWT
    /*
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }
    */
}