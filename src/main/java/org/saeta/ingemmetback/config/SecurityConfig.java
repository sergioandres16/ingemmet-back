package org.saeta.ingemmetback.config;

import org.saeta.ingemmetback.security.JwtAuthEntryPoint;
import org.saeta.ingemmetback.security.JwtTokenFilter;
import org.saeta.ingemmetback.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Bean para codificar contraseñas con BCrypt
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuración principal de Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Deshabilitamos CSRF para APIs stateless
        http.csrf(csrf -> csrf.disable());

        // Configuramos las rutas permitidas
        http.authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
        );

        // No guardamos sesión en servidor (100% stateless)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Manejador de errores de autenticación
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint));

        // Filtro para JWT antes de UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // Otras personalizaciones (Opcional)
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}