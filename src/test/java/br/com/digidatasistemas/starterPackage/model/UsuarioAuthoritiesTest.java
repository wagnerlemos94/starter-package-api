package br.com.digidatasistemas.starterPackage.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioAuthoritiesTest {

    @Test
    void deveGerarRoleEPermissoesAtivasSemDuplicidade() {
        Permissao visualizar = permissao("VIEW", true);
        Recurso usuario = recurso("USUARIO", true);
        PerfilRecurso perfilRecurso = PerfilRecurso.builder()
                .recurso(usuario)
                .permissoes(List.of(visualizar, visualizar))
                .build();
        Usuario autenticado = usuario(perfil(true, List.of(perfilRecurso)));

        Collection<? extends GrantedAuthority> authorities = autenticado.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(possui(authorities, "ROLE_ADMIN"));
        assertTrue(possui(authorities, "USUARIO:VIEW"));
    }

    @Test
    void naoDeveConcederAuthorityDeRecursoOuPermissaoInativos() {
        PerfilRecurso recursoInativo = PerfilRecurso.builder()
                .recurso(recurso("USUARIO", false))
                .permissoes(List.of(permissao("VIEW", true)))
                .build();
        PerfilRecurso permissaoInativa = PerfilRecurso.builder()
                .recurso(recurso("PERFIL", true))
                .permissoes(List.of(permissao("UPDATE", false)))
                .build();

        Collection<? extends GrantedAuthority> authorities = usuario(
                perfil(true, List.of(recursoInativo, permissaoInativa))
        ).getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(possui(authorities, "ROLE_ADMIN"));
    }

    @Test
    void perfilInativoNaoDeveHabilitarUsuarioNemConcederAuthorities() {
        Usuario usuario = usuario(perfil(false, null));

        assertFalse(usuario.isEnabled());
        assertTrue(usuario.getAuthorities().isEmpty());
    }

    private boolean possui(Collection<? extends GrantedAuthority> authorities, String authority) {
        return authorities.stream().anyMatch(item -> item.getAuthority().equals(authority));
    }

    private Usuario usuario(Perfil perfil) {
        return Usuario.builder()
                .cpf("00000000535")
                .password("hash")
                .name("Usuário")
                .active(true)
                .perfil(perfil)
                .build();
    }

    private Perfil perfil(boolean ativo, List<PerfilRecurso> recursos) {
        return Perfil.builder()
                .id(UUID.randomUUID())
                .nome("Administrador")
                .chave("ADMIN")
                .ativo(ativo)
                .perfilRecursos(recursos)
                .build();
    }

    private Recurso recurso(String chave, boolean ativo) {
        return Recurso.builder().id(UUID.randomUUID()).chave(chave).ativo(ativo).build();
    }

    private Permissao permissao(String chave, boolean ativo) {
        return Permissao.builder().id(UUID.randomUUID()).chave(chave).ativo(ativo).build();
    }
}
