package br.com.digidatasistemas.starterPackage.security;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioAutenticadoTest {

    private final UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado();

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornarUsuarioAutenticado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder()
                .id(id)
                .cpf("00000000000")
                .name("Usuario Teste")
                .active(true)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, List.of())
        );

        assertSame(usuario, usuarioAutenticado.get());
        assertEquals(id, usuarioAutenticado.getId());
        assertEquals("00000000000", usuarioAutenticado.getCpf());
    }

    @Test
    void deveRejeitarQuandoNaoExisteAutenticacao() {
        assertThrows(AccessDeniedException.class, usuarioAutenticado::get);
    }

    @Test
    void deveRejeitarPrincipalQueNaoSejaUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of())
        );

        assertThrows(AccessDeniedException.class, usuarioAutenticado::get);
    }
}
