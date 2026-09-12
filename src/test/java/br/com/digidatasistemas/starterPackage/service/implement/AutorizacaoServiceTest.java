package br.com.digidatasistemas.starterPackage.service.implement;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AutorizacaoServiceTest {

    private final AutorizacaoService service = new AutorizacaoService();

    @Test
    void deveAutorizarQuandoUsuarioPossuiAutoridadeExata() {
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "usuario", null, List.of(new SimpleGrantedAuthority("USUARIO:VIEW")));

        assertThat(service.hasPermission(authentication, "USUARIO", "VIEW")).isTrue();
    }

    @Test
    void deveNegarQuandoAutoridadeNaoCorresponde() {
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "usuario", null, List.of(new SimpleGrantedAuthority("USUARIO:CREATE")));

        assertThat(service.hasPermission(authentication, "USUARIO", "VIEW")).isFalse();
    }

    @Test
    void deveNegarUsuarioNaoAutenticado() {
        var authentication = UsernamePasswordAuthenticationToken.unauthenticated("usuario", null);

        assertThat(service.hasPermission(authentication, "USUARIO", "VIEW")).isFalse();
    }

    @Test
    void deveNegarQuandoNaoExisteAutenticacao() {
        assertThat(service.hasPermission(null, "USUARIO", "VIEW")).isFalse();
    }
}
