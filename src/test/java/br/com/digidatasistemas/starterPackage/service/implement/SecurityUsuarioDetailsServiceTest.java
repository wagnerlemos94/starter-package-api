package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_USUARIO_NAO_ENCONTRADO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityUsuarioDetailsServiceTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final SecurityUsuarioDetailsService service = new SecurityUsuarioDetailsService(repository);

    @Test
    void deveCarregarUsuarioPeloCpf() {
        var usuario = spy(Usuario.builder().cpf("00000000535").build());
        when(repository.findByCpf("00000000535")).thenReturn(Optional.of(usuario));

        assertThat(service.loadUserByUsername("00000000535")).isSameAs(usuario);
        verify(usuario).getAuthorities();
    }

    @Test
    void deveFalharQuandoCpfNaoExiste() {
        when(repository.findByCpf("00000000535")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("00000000535"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(MSG_USUARIO_NAO_ENCONTRADO);
    }
}
