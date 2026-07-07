package com.WW.sicurezza;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.WW.entities.Ruolo;
import com.WW.entities.Utente;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servizio che crea e valida i token JWT usati come Bearer token.
 */
@Service
public class ServizioJwt {

    private final SecretKey chiaveSegreta;
    private final long scadenzaSecondi;

    /**
     * Inizializza il servizio JWT usando le proprieta dell'applicazione.
     *
     * @param segretoBase64   chiave HMAC codificata in Base64
     * @param scadenzaSecondi durata del token in secondi
     */
    public ServizioJwt(
            @Value("${app.security.jwt.secret}") String segretoBase64,
            @Value("${app.security.jwt.expiration-seconds:3600}") long scadenzaSecondi) {
        this.chiaveSegreta = Keys.hmacShaKeyFor(Decoders.BASE64.decode(segretoBase64));
        this.scadenzaSecondi = scadenzaSecondi;
    }

    /**
     * Crea un JWT firmato per l'utente autenticato.
     *
     * @param utente utente autenticato
     * @return token JWT in formato compatto
     */
    public String creaToken(Utente utente) {
        Instant oraCorrente = Instant.now();
        Instant scadenza = oraCorrente.plusSeconds(scadenzaSecondi);
        Ruolo ruolo = utente.getRuolo();

        return Jwts.builder()
                .subject(utente.getId().toString())
                .claim("email", utente.getEmail())
                .claim("role", utente.getRuolo().getRuolo())
                .issuedAt(Date.from(oraCorrente))
                .expiration(Date.from(scadenza))
                .signWith(chiaveSegreta)
                .compact();
    }

    /**
     * Legge un JWT e ne valida firma, emittente e scadenza.
     *
     * @param token token JWT in formato compatto
     * @return claim validati del token
     */
    public Claims leggiEValida(String token) {
        return Jwts.parser()
                .verifyWith(chiaveSegreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Restituisce la durata configurata del token.
     *
     * @return durata del token in secondi
     */
    public long ottieniScadenzaSecondi() {
        return scadenzaSecondi;
    }

    public Map<String, Object> estraiMappaClaims(String token) {
        Claims claims = leggiEValida(token);

        Map<String, Object> mappaClaims = new HashMap<>();
        mappaClaims.put("userId", Integer.valueOf(claims.getSubject()));
        mappaClaims.put("email", claims.get("email", String.class));
        mappaClaims.put("role", claims.get("role", String.class));
        mappaClaims.put("expiresAt", claims.getExpiration());

        return mappaClaims;
    }
}
