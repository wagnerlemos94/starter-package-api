package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidata.crud.service.ICrudService;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class BaseCrudControllerTest {

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void devePermitirQuandoAutoridadeCorrespondeAoRecursoEOperacao() {
        autenticar(true, "USUARIO:VIEW");

        assertThatCode(() -> new ControllerComRecurso().verificar("VIEW"))
                .doesNotThrowAnyException();
    }

    @Test
    void deveNegarQuandoUsuarioNaoPossuiAutoridade() {
        autenticar(true, "USUARIO:CREATE");

        assertThatThrownBy(() -> new ControllerComRecurso().verificar("DELETE"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Usuário não possui a permissão: USUARIO:DELETE");
    }

    @Test
    void deveNegarQuandoNaoHaAutenticacao() {
        assertThatThrownBy(() -> new ControllerComRecurso().verificar("VIEW"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Usuário não autenticado.");
    }

    @Test
    void deveNegarQuandoAuthenticationNaoEstaAutenticado() {
        autenticar(false);

        assertThatThrownBy(() -> new ControllerComRecurso().verificar("VIEW"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deveFalharQuandoControllerNaoDeclaraRecurso() {
        assertThatThrownBy(() -> new ControllerSemRecurso().verificar("VIEW"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não possui @ResourcePermission");
    }

    private void autenticar(boolean autenticado, String... autoridades) {
        var authorities = List.of(autoridades).stream().map(SimpleGrantedAuthority::new).toList();
        var authentication = autenticado
                ? UsernamePasswordAuthenticationToken.authenticated("usuario", null, authorities)
                : UsernamePasswordAuthenticationToken.unauthenticated("usuario", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @RecursoPermissao("USUARIO")
    private static final class ControllerComRecurso extends ControllerBaseTeste {
    }

    private static final class ControllerSemRecurso extends ControllerBaseTeste {
    }

    private abstract static class ControllerBaseTeste extends BaseCrudController<Object, Object, Object> {

        @SuppressWarnings("unchecked")
        private ControllerBaseTeste() {
            super(mock(ICrudService.class), mock(IRequest.class), mock(IResponse.class));
        }

        void verificar(String permissao) {
            checkCrudPermission(permissao);
        }
    }
}
