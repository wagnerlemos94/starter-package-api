package br.com.digidatasistemas.starterPackage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityErrorHandlersTest {

    private RestAuthenticationEntryPoint authenticationEntryPoint;
    private RestAccessDeniedHandler accessDeniedHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SecurityErrorResponseWriter writer = new SecurityErrorResponseWriter(objectMapper);
        authenticationEntryPoint = new RestAuthenticationEntryPoint(writer);
        accessDeniedHandler = new RestAccessDeniedHandler(writer);
    }

    @Test
    void deveRetornarContratoPadraoQuandoNaoAutenticado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authenticationEntryPoint.commence(request, response, new BadCredentialsException("interno"));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Autenticação necessária."));
        assertTrue(response.getContentAsString().contains("errorId"));
    }

    @Test
    void deveRetornarContratoPadraoQuandoSemPermissao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        accessDeniedHandler.handle(request, response, new AccessDeniedException("interno"));

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("não tem permissão"));
        assertTrue(response.getContentAsString().contains("errorId"));
    }
}
