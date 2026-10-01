package br.com.digidatasistemas.starterPackage.integration;

import br.com.digidatasistemas.starterPackage.configuration.SecurityConfig;
import br.com.digidatasistemas.starterPackage.controller.AuthController;
import br.com.digidatasistemas.starterPackage.controller.UsuarioController;
import br.com.digidatasistemas.starterPackage.controller.dto.request.UsuarioRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.UsuarioResponse;
import br.com.digidatasistemas.starterPackage.security.UsuarioAutenticado;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import br.com.digidatasistemas.starterPackage.service.IAutorizacaoService;
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
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.MSG_DADOS_INVALIDOS;
import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.MSG_USUARIO_OU_SENHA_INVALIDOS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        UsuarioController.class,
        SecurityTestController.class
}, properties = "spring.profiles.default=test")
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityErrorResponseWriter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        ApiExceptionHandler.class,
        UsuarioRequest.class,
        UsuarioResponse.class,
        UsuarioAutenticado.class
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

    @MockitoBean
    private IUsuarioService<Usuario> usuarioService;

    @MockitoBean
    private IAutorizacaoService autorizacaoService;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void propriaContaSemTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/usuario/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/usuario/me").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Novo nome\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveConsultarPropriaContaSemPermissoesAdministrativas() throws Exception {
        Usuario usuario = usuarioSemPermissao();
        usuario.setId(UUID.randomUUID());
        configurarTokenValido(usuario);
        when(usuarioService.findById(usuario.getId())).thenReturn(usuario);

        mockMvc.perform(get("/usuario/me").header("Authorization", "Bearer valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId().toString()))
                .andExpect(jsonPath("$.profile").value("Básico"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verifyNoInteractions(autorizacaoService);
    }

    @Test
    void deveUsarIdentidadeAutenticadaEIgnorarCamposAdministrativosNaPropriaEdicao() throws Exception {
        Usuario usuario = usuarioSemPermissao();
        usuario.setId(UUID.randomUUID());
        configurarTokenValido(usuario);
        when(usuarioService.updateCurrent(eq(usuario.getId()), eq("Novo nome"), isNull(), isNull()))
                .thenReturn(usuario);

        mockMvc.perform(put("/usuario/me").header("Authorization", "Bearer valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Novo nome","id":"00000000-0000-0000-0000-000000000001",
                                 "cpf":"11111111111","profileId":"00000000-0000-0000-0000-000000000002","active":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId().toString()))
                .andExpect(jsonPath("$.cpf").value(usuario.getCpf()))
                .andExpect(jsonPath("$.active").value(true));
        verify(usuarioService).updateCurrent(usuario.getId(), "Novo nome", null, null);
        verifyNoInteractions(autorizacaoService);
    }

    @Test
    void propriaEdicaoComNomeOuSenhaInvalidosDeveRetornarErrosPorCampo() throws Exception {
        configurarTokenValido(usuarioSemPermissao());
        mockMvc.perform(put("/usuario/me").header("Authorization", "Bearer valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \",\"password\":\"curta\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
        mockMvc.perform(put("/usuario/me").header("Authorization", "Bearer valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Novo nome\",\"password\":\"        \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
        verifyNoInteractions(usuarioService);
    }

    @Test
    void propriaEdicaoNaoDeveLiberarEdicaoAdministrativa() throws Exception {
        Usuario usuario = usuarioSemPermissao();
        configurarTokenValido(usuario);
        mockMvc.perform(put("/usuario/" + UUID.randomUUID()).header("Authorization", "Bearer valido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Outro usuário","cpf":"00000000535",
                                 "profileId":"00000000-0000-0000-0000-000000000001","active":true}
                                """))
                .andExpect(status().isForbidden());
        verifyNoInteractions(usuarioService);
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
        when(authService.login(any())).thenThrow(new UnauthorizedException(MSG_USUARIO_OU_SENHA_INVALIDOS));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"00000000535","password":"senha-incorreta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value(MSG_USUARIO_OU_SENHA_INVALIDOS))
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
                .andExpect(jsonPath("$.message").value(MSG_DADOS_INVALIDOS))
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
