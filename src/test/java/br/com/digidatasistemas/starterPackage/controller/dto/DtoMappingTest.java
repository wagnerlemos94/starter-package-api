package br.com.digidatasistemas.starterPackage.controller.dto;

import br.com.digidatasistemas.starterPackage.controller.dto.request.PerfilRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.request.PermissaoRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.request.RecursoRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.request.UsuarioRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.PerfilResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.response.PermissaoResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.response.RecursoResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.response.UsuarioResponse;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMappingTest {

    @Test
    void deveConverterPerfilRequestComAssociacoes() {
        UUID perfilId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID permissaoId = UUID.randomUUID();
        var request = new PerfilRequest();
        request.setId(perfilId);
        request.setNome("Administrador");
        request.setDescricao("Acesso total");
        request.setAtivo(true);
        request.setPerfilRecurso(Map.of(recursoId, List.of(permissaoId)));

        Perfil perfil = request.to(request);

        assertThat(perfil.getId()).isEqualTo(perfilId);
        assertThat(perfil.getNome()).isEqualTo("Administrador");
        assertThat(perfil.getPerfilRecursos()).singleElement().satisfies(associacao -> {
            assertThat(associacao.getPerfil()).isSameAs(perfil);
            assertThat(associacao.getRecurso().getId()).isEqualTo(recursoId);
            assertThat(associacao.getPermissoes()).extracting(Permissao::getId).containsExactly(permissaoId);
        });
        assertThat(request.to(List.of(request))).singleElement()
                .extracting(Perfil::getId).isEqualTo(perfilId);
    }

    @Test
    void deveAceitarAssociacoesEPermissoesNulasNoPerfilRequest() {
        var semAssociacoes = new PerfilRequest();
        semAssociacoes.setPerfilRecurso(null);
        assertThat(semAssociacoes.to(semAssociacoes).getPerfilRecursos()).isEmpty();

        var semPermissoes = new PerfilRequest();
        semPermissoes.setPerfilRecurso(new java.util.HashMap<>());
        semPermissoes.getPerfilRecurso().put(UUID.randomUUID(), null);
        assertThat(semPermissoes.to(semPermissoes).getPerfilRecursos())
                .singleElement().extracting(PerfilRecurso::getPermissoes).asList().isEmpty();
    }

    @Test
    void deveConverterRequestsSimples() {
        UUID id = UUID.randomUUID();
        UUID perfilId = UUID.randomUUID();
        var usuarioRequest = new UsuarioRequest();
        usuarioRequest.setId(id);
        usuarioRequest.setCpf("00000000535");
        usuarioRequest.setName("Usuário");
        usuarioRequest.setProfileId(perfilId);
        usuarioRequest.setPassword("senha-segura");
        usuarioRequest.setActive(true);

        Usuario usuario = usuarioRequest.to(usuarioRequest);
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getPerfil().getId()).isEqualTo(perfilId);
        assertThat(usuarioRequest.to(List.of(usuarioRequest))).singleElement()
                .extracting(Usuario::getId).isEqualTo(id);

        var recursoRequest = new RecursoRequest();
        recursoRequest.setId(id);
        recursoRequest.setNome("aluno");
        recursoRequest.setDescricao("Cadastro de alunos");
        recursoRequest.setAtivo(true);
        Recurso recurso = recursoRequest.to(recursoRequest);
        assertThat(recurso.getChave()).isEqualTo("ALUNO");
        assertThat(recursoRequest.to(List.of(recursoRequest))).singleElement()
                .extracting(Recurso::getId).isEqualTo(id);

        var permissaoRequest = new PermissaoRequest();
        permissaoRequest.setId(id);
        permissaoRequest.setNome("Visualizar");
        permissaoRequest.setChave("VIEW");
        permissaoRequest.setDescricao("Permite visualizar");
        permissaoRequest.setAtivo(true);
        Permissao permissao = permissaoRequest.to(permissaoRequest);
        assertThat(permissao.getChave()).isEqualTo("VIEW");
        assertThat(permissaoRequest.to(List.of(permissaoRequest))).singleElement()
                .extracting(Permissao::getId).isEqualTo(id);
    }

    @Test
    void deveConverterResponsesComRelacionamentos() {
        var permissao = Permissao.builder().id(UUID.randomUUID()).nome("Visualizar").chave("VIEW").ativo(true).build();
        var recurso = Recurso.builder().id(UUID.randomUUID()).nome("Aluno").chave("ALUNO").ativo(true).build();
        var associacao = PerfilRecurso.builder().id(UUID.randomUUID()).recurso(recurso).permissoes(List.of(permissao)).build();
        var perfil = Perfil.builder().id(UUID.randomUUID()).nome("Operador").chave("OPERADOR")
                .perfilRecursos(List.of(associacao)).ativo(true).build();
        var usuario = Usuario.builder().id(UUID.randomUUID()).cpf("00000000535").name("Usuário")
                .active(true).perfil(perfil).build();

        PerfilResponse perfilResponse = new PerfilResponse().to(perfil);
        assertThat(perfilResponse.getPerfilRecursoResponse()).singleElement().satisfies(item -> {
            assertThat(item.getRecursoId()).isEqualTo(recurso.getId());
            assertThat(item.getPermissoes()).extracting(PermissaoResponse::getChave).containsExactly("VIEW");
        });
        assertThat(new PerfilResponse().to(List.of(perfil))).hasSize(1);
        assertThat(new RecursoResponse().to(List.of(recurso))).singleElement().extracting(RecursoResponse::getChave).isEqualTo("ALUNO");
        assertThat(new PermissaoResponse().to(List.of(permissao))).singleElement().extracting(PermissaoResponse::getChave).isEqualTo("VIEW");
        assertThat(new UsuarioResponse().to(List.of(usuario))).singleElement().satisfies(response -> {
            assertThat(response.getCpf()).isEqualTo("00000000535");
            assertThat(response.getProfile().getNome()).isEqualTo("Operador");
        });
    }

    @Test
    void deveConverterRelacionamentosNulosNosResponses() {
        var perfil = Perfil.builder().perfilRecursos(null).build();
        assertThat(new PerfilResponse(perfil).getPerfilRecursoResponse()).isEmpty();

        var recurso = Recurso.builder().build();
        var associacao = PerfilRecurso.builder().recurso(recurso).permissoes(null).build();
        assertThat(new br.com.digidatasistemas.starterPackage.controller.dto.response.PerfilRecursoResponse(associacao)
                .getPermissoes()).isEmpty();

        var response = new UsuarioResponse().to(Usuario.builder().perfil(null).build());
        assertThat(response.getProfile()).isNotNull();
    }
}
