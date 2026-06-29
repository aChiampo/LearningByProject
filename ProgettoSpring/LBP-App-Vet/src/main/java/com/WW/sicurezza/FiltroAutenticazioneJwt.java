package com.WW.sicurezza;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro che autentica le richieste HTTP leggendo il token JWT dall'header Authorization.
 */
@Component
@RequiredArgsConstructor
public class FiltroAutenticazioneJwt extends OncePerRequestFilter {

    private static final String HEADER_AUTORIZZAZIONE = "Authorization";
    private static final String PREFISSO_BEARER = "Bearer ";

    private final ServizioJwt servizioJwt;

    /**
     * Estrae il Bearer token, lo valida e popola il contesto di sicurezza di Spring.
     *
     * @param richiesta richiesta HTTP in ingresso
     * @param risposta risposta HTTP in uscita
     * @param catenaFiltri filtri successivi da eseguire
     * @throws ServletException se la catena dei filtri fallisce
     * @throws IOException se la lettura o scrittura HTTP fallisce
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest richiesta,
            HttpServletResponse risposta,
            FilterChain catenaFiltri) throws ServletException, IOException {

        String headerAutorizzazione = richiesta.getHeader(HEADER_AUTORIZZAZIONE);

        if (headerAutorizzazione == null || !headerAutorizzazione.startsWith(PREFISSO_BEARER)) {
            catenaFiltri.doFilter(richiesta, risposta);
            return;
        }

        String token = headerAutorizzazione.substring(PREFISSO_BEARER.length());

        try {
            Claims claims = servizioJwt.leggiEValida(token);
            String idUtente = claims.getSubject();
            String ruolo = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken autenticazione =
                    new UsernamePasswordAuthenticationToken(
                            idUtente,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + ruolo))
                    );
            autenticazione.setDetails(new WebAuthenticationDetailsSource().buildDetails(richiesta));

            SecurityContextHolder.getContext().setAuthentication(autenticazione);
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
        }

        catenaFiltri.doFilter(richiesta, risposta);
    }
}
