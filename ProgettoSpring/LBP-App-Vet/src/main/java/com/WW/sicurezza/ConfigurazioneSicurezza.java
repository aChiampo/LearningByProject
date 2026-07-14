package com.WW.sicurezza;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Configurazione di Spring Security per autenticazione stateless con Bearer token.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class ConfigurazioneSicurezza {

    private final FiltroAutenticazioneJwt filtroAutenticazioneJwt;

    /**
     * Definisce le regole di autorizzazione e registra il filtro JWT.
     *
     * @param http builder di configurazione HTTP di Spring Security
     * @return catena dei filtri di sicurezza configurata
     * @throws Exception se Spring Security non riesce a creare la catena
     */
    @Bean
    public SecurityFilterChain catenaFiltriSicurezza(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurazioneCors()))
                .sessionManagement(sessione -> sessione.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizzazioni -> autorizzazioni
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/signin",
                                "/api/autenticazione/login",
                                "/api/autenticazione/signin"
                        ).permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filtroAutenticazioneJwt, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Crea il componente usato per cifrare e verificare le password.
     *
     * @return codificatore BCrypt per le password
     */
    @Bean
    public PasswordEncoder codificatorePassword() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Permette al frontend React di chiamare le API inviando l'header Authorization.
     *
     * @return configurazione CORS dell'applicazione
     */
    @Bean
    public CorsConfigurationSource configurazioneCors() {
        CorsConfiguration configurazione = new CorsConfiguration();
        configurazione.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configurazione.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configurazione.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configurazione.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource origine = new UrlBasedCorsConfigurationSource();
        origine.registerCorsConfiguration("/**", configurazione);
        return origine;
    }
}
