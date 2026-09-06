package br.com.digidatasistemas.starterPackage.security;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.service.IJwtService;
import br.com.digidatasistemas.starterPackage.service.IUsuarioDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IJwtService jwtService;

    @Mock
    private IUsuarioDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService,
                new SecurityErrorResponseWriter(objectMapper)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarTokenValido() throws Exception {
        MockHttpServletRequest request = requestComToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuario(true);
        when(jwtService.extractUsername("token")).thenReturn(usuario.getCpf());
        when(userDetailsService.loadUserByUsername(usuario.getCpf())).thenReturn(usuario);
        when(jwtService.isTokenValid("token", usuario)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertSame(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveRetornarUnauthorizedParaTokenInvalidoOuUsuarioInativo() throws Exception {
        MockHttpServletRequest request = requestComToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuario(false);
        when(jwtService.extractUsername("token")).thenReturn(usuario.getCpf());
        when(userDetailsService.loadUserByUsername(usuario.getCpf())).thenReturn(usuario);
        when(jwtService.isTokenValid("token", usuario)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token inválido ou usuário inativo."));
        assertTrue(response.getContentAsString().contains("errorId"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void deveContinuarSemTokenParaSecurityDecidirAcesso() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    private MockHttpServletRequest requestComToken() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
        return request;
    }

    private Usuario usuario(boolean active) {
        return Usuario.builder()
                .cpf("00000000535")
                .name("Usuário Teste")
                .password("hash")
                .active(active)
                .build();
    }
}
