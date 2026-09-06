package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private final SecretKey key;
    private final long expirationMillis;
    private final Clock clock;

    @Autowired
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long expirationMillis
    ) {
        this(secret, expirationMillis, Clock.systemUTC());
    }

    JwtService(String secret, long expirationMillis, Clock clock) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("security.jwt.secret deve possuir pelo menos 32 bytes");
        }
        if (expirationMillis <= 0) {
            throw new IllegalArgumentException("security.jwt.expiration deve ser maior que zero");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
        this.clock = clock;
    }

    @Override
    public String generateToken(UserDetails usuario) {
        Instant issuedAt = clock.instant();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails usuario) {
        Claims claims = extractClaims(token);

        return usuario.isEnabled()
                && usuario.getUsername().equals(claims.getSubject())
                && !claims.getExpiration().before(Date.from(clock.instant()));
    }

    @Override
    public long getExpirationMillis() {
        return expirationMillis;
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
