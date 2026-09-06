package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.controller.dto.request.LoginRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;
import br.com.digidatasistemas.starterPackage.exception.UnauthorizedException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private IJwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtService);
    }

    @Test
    void deveAutenticarEGerarTokenComTempoDeExpiracao() {
        Usuario usuario = usuarioAtivo();
        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(usuario)).thenReturn("token-jwt");
        when(jwtService.getExpirationMillis()).thenReturn(3_600_000L);

        LoginResponse response = authService.login(new LoginRequest("00000000535", "senha123"));

        assertEquals("token-jwt", response.token());
        assertEquals(3_600_000L, response.expiresInToken());
        assertEquals(usuario.getCpf(), response.username());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void devePadronizarCredenciaisInvalidas() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("detalhe interno"));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(new LoginRequest("00000000535", "errada"))
        );

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    void deveInformarUsuarioInativo() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("disabled"));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(new LoginRequest("00000000535", "senha123"))
        );

        assertEquals("Usuário inativo", exception.getMessage());
    }

    private Usuario usuarioAtivo() {
        Perfil perfil = Perfil.builder()
                .nome("Administrador")
                .chave("ADMIN")
                .perfilRecursos(new ArrayList<>())
                .build();

        return Usuario.builder()
                .cpf("00000000535")
                .name("Usuário Teste")
                .password("hash")
                .active(true)
                .perfil(perfil)
                .build();
    }
}
