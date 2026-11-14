package hackhub.app.Infrastructure.Utils;

import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Infrastructure.Config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private final SecretKey key;
    private final long expirationMs;

    /** HMAC-SHA richiede una chiave di almeno 256 bit (RFC 7518, sez. 3.2). */
    private static final int MIN_SECRET_BYTES = 32;

    public JwtService(JwtProperties properties) {
        String secret = properties.getSecret();
        if (secret == null || secret.isBlank() || secret.contains("${")) {
            throw new IllegalStateException(
                "JWT_SECRET non configurato: imposta la variabile d'ambiente JWT_SECRET (almeno 32 byte, consigliati 64 caratteri casuali).");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                "JWT_SECRET troppo corto: servono almeno " + MIN_SECRET_BYTES + " byte (consigliati 64 caratteri casuali).");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = properties.getExpiration();
    }

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getId())
                .claim("ruolo", user.getRuolo() != null ? user.getRuolo().name() : null)
                .claim("nome", user.getNome())
                .claim("cognome", user.getCognome())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    @Override
    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String extractRuolo(String token) {
        Object ruolo = parseClaims(token).get("ruolo");
        return ruolo != null ? ruolo.toString() : null;
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

