package br.com.digidatasistemas.starterPackage.integration;

import br.com.digidatasistemas.starterPackage.configuration.SecurityConfig;
import br.com.digidatasistemas.starterPackage.controller.AuthController;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;
import br.com.digidatasistemas.starterPackage.exception.ApiExceptionHandler;
import br.com.digidatasistemas.starterPackage.exception.UnauthorizedException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.security.JwtAuthenticationFilter;
import br.com.digidatasistemas.starterPackage.security.RestAccessDeniedHandler;
import br.com.digidatasistemas.starterPackage.security.RestAuthenticationEntryPoint;
import br.com.digidatasistemas.starterPackage.security.SecurityErrorResponseWriter;
import br.com.digidatasistemas.starterPackage.service.IAuthService;
import br.com.digidatasistemas.starterPackage.service.IJwtService;
import br.com.digidatasistemas.starterPackage.service.IUsuarioDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        SecurityTestController.class
}, properties = "spring.profiles.default=test")
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityErrorResponseWriter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        ApiExceptionHandler.class
})
class SecurityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private IUsuarioDetailsService usuarioDetailsService;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginValidoDeveRetornarTokenEExpiracao() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                "token-jwt",
                3_600_000L,
                "Usuário Teste",
                "00000000535",
                Map.of("USUARIO", List.of("VIEW"))
        ));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"00000000535","password":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"))
                .andExpect(jsonPath("$.expiresInToken").value(3_600_000));
    }

    @Test
    void loginInvalidoDeveRetornarContrato401() throws Exception {
        when(authService.login(any())).thenThrow(new UnauthorizedException("Usuário ou senha inválidos"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"00000000535","password":"senha-incorreta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos"))
                .andExpect(jsonPath("$.errorId").isNotEmpty());
    }

    @Test
    void loginComDadosInvalidosDeveRetornarErrosPorCampo() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"123","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors[?(@.field == 'cpf')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
    }

    @Test
    void endpointProtegidoSemTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/test/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Autenticação necessária."))
                .andExpect(jsonPath("$.errorId").isNotEmpty());
    }

    @Test
    void tokenInvalidoDeveRetornar401() throws Exception {
        when(jwtService.extractUsername("invalido"))
                .thenThrow(new MalformedJwtException("token inválido"));

        mockMvc.perform(get("/test/protected")
                        .header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token inválido."));
    }

    @Test
    void tokenExpiradoDeveRetornar401() throws Exception {
        when(jwtService.extractUsername("expirado"))
                .thenThrow(new ExpiredJwtException(null, null, "expirado"));

        mockMvc.perform(get("/test/protected")
                        .header("Authorization", "Bearer expirado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token expirado."));
    }

    @Test
    void usuarioSemPermissaoDeveRetornar403() throws Exception {
        configurarTokenValido(usuarioSemPermissao());

        mockMvc.perform(get("/test/permission")
                        .header("Authorization", "Bearer valido"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void usuarioComPermissaoDeveAcessarEndpoint() throws Exception {
        configurarTokenValido(usuarioComPermissao());

        mockMvc.perform(get("/test/permission")
                        .header("Authorization", "Bearer valido"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void conflitoDeveRetornar409() throws Exception {
        configurarTokenValido(usuarioComPermissao());

        mockMvc.perform(get("/test/conflict")
                        .header("Authorization", "Bearer valido"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void recursoInexistenteDeveRetornar404() throws Exception {
        configurarTokenValido(usuarioComPermissao());

        mockMvc.perform(get("/test/not-found")
                        .header("Authorization", "Bearer valido"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    private void configurarTokenValido(Usuario usuario) {
        when(jwtService.extractUsername("valido")).thenReturn(usuario.getCpf());
        when(usuarioDetailsService.loadUserByUsername(usuario.getCpf())).thenReturn(usuario);
        when(jwtService.isTokenValid("valido", usuario)).thenReturn(true);
    }

    private Usuario usuarioSemPermissao() {
        return usuarioComPerfil(Perfil.builder()
                .nome("Básico")
                .chave("BASICO")
                .ativo(true)
                .perfilRecursos(List.of())
                .build());
    }

    private Usuario usuarioComPermissao() {
        Permissao permissao = Permissao.builder().chave("VIEW").ativo(true).build();
        Recurso recurso = Recurso.builder().chave("USUARIO").ativo(true).build();
        PerfilRecurso associacao = PerfilRecurso.builder()
                .recurso(recurso)
                .permissoes(List.of(permissao))
                .build();
        Perfil perfil = Perfil.builder()
                .nome("Administrador")
                .chave("ADMIN")
                .ativo(true)
                .perfilRecursos(List.of(associacao))
                .build();
        return usuarioComPerfil(perfil);
    }

    private Usuario usuarioComPerfil(Perfil perfil) {
        return Usuario.builder()
                .cpf("00000000535")
                .name("Usuário Teste")
                .password("hash")
                .active(true)
                .perfil(perfil)
                .build();
    }

}
