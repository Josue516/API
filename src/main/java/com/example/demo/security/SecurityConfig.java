package com.example.demo.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private FirebaseAuthFilter firebaseAuthFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**", "/api/health").permitAll()
            	    .requestMatchers(HttpMethod.POST, "/api/pagos/crear").authenticated()
            	    .requestMatchers(HttpMethod.POST, "/api/pagos/confirmar").authenticated()
            	    // Endpoints públicos para clientes (solo datos activos)
            	    .requestMatchers(HttpMethod.GET, "/api/peliculas/cartelera").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/salas/activas").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/funciones/activas").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/sedes/activas").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/funciones/*/asientos").permitAll() 
            	    .requestMatchers(HttpMethod.GET, "/api/funciones/*").permitAll()   
            	    .requestMatchers("/api/reservas/mis-reservas").authenticated()
            	    .requestMatchers(HttpMethod.POST, "/api/reservas").authenticated()
            	    .requestMatchers(HttpMethod.POST, "/api/reservas/*/confirmar").authenticated()
            	    .requestMatchers(HttpMethod.DELETE, "/api/reservas/*").authenticated()
            	    // Todo lo demás requiere ADMIN
            	    .requestMatchers("/api/reservas/**").hasRole("ADMIN")
            	    .requestMatchers("/api/peliculas/**").hasRole("ADMIN")
            	    .requestMatchers("/api/salas/**").hasRole("ADMIN")
            	    .requestMatchers("/api/sedes/**").hasRole("ADMIN")
            	    .requestMatchers("/api/funciones/**").hasRole("ADMIN")
            	    .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "https://josue516.github.io",
            "https://josue516.github.io/AppCine",
            "null", // apps móviles
            "*"     // cualquier origen
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Use Firebase Auth");
        };
    }
    
}