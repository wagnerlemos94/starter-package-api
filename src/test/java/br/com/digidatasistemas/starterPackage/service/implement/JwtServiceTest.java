package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "chave-de-teste-com-pelo-menos-32-bytes-segura";
    private static final Instant AGORA = Instant.parse("2026-09-05T12:00:00Z");

    @Test
    void deveUsarExpiracaoConfiguradaNoToken() {
        long expirationMillis = 3_600_000L;
        JwtService service = service(expirationMillis);
        Usuario usuario = usuario(true);

        String token = service.generateToken(usuario);
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .clock(() -> java.util.Date.from(AGORA))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(AGORA, claims.getIssuedAt().toInstant());
        assertEquals(AGORA.plusMillis(expirationMillis), claims.getExpiration().toInstant());
        assertEquals(expirationMillis, service.getExpirationMillis());
    }

    @Test
    void deveValidarTokenDoUsuarioAtivo() {
        JwtService service = service(3_600_000L);
        Usuario usuario = usuario(true);

        assertTrue(service.isTokenValid(service.generateToken(usuario), usuario));
    }

    @Test
    void deveInvalidarTokenQuandoUsuarioEstaInativo() {
        JwtService service = service(3_600_000L);
        Usuario usuarioAtivo = usuario(true);
        String token = service.generateToken(usuarioAtivo);
        Usuario usuarioInativo = usuario(false);

        assertFalse(service.isTokenValid(token, usuarioInativo));
    }

    @Test
    void deveRejeitarConfiguracoesInseguras() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("curta", 1000, Clock.systemUTC()));
        assertThrows(IllegalArgumentException.class, () -> new JwtService(SECRET, 0, Clock.systemUTC()));
    }

    private JwtService service(long expirationMillis) {
        return new JwtService(
                SECRET,
                expirationMillis,
                Clock.fixed(AGORA, ZoneOffset.UTC)
        );
    }

    private Usuario usuario(boolean active) {
        Perfil perfil = Perfil.builder()
                        .nome("Administrador")
                        .chave("ADMIN")
                        .ativo(true)
                        .build();

        return Usuario.builder()
                .cpf("00000000535")
                .name("Usuário Teste")
                .password("hash")
                .active(active)
                .perfil(perfil)
                .build();
    }
}
