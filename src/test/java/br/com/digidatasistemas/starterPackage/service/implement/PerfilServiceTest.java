package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.exception.ResourceNotFoundException;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.exception.ConflictException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.repository.PerfilRepository;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import br.com.digidatasistemas.starterPackage.service.IRecursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository repository;

    @Mock
    private IRecursoService<Recurso> recursoService;

    @Mock
    private IPermissaoService<Permissao> permissaoService;

    private PerfilService perfilService;

    @BeforeEach
    void setUp() {
        perfilService = new PerfilService(repository, recursoService, permissaoService);
    }

    @Test
    void deveInicializarRelacionamentosAoBuscarPerfil() {
        UUID id = UUID.randomUUID();
        Perfil perfil = mock(Perfil.class);
        PerfilRecurso associacao = mock(PerfilRecurso.class);
        Recurso recurso = mock(Recurso.class);
        List<Permissao> permissoes = mock(List.class);
        when(repository.findById(id)).thenReturn(Optional.of(perfil));
        when(perfil.getPerfilRecursos()).thenReturn(List.of(associacao));
        when(associacao.getRecurso()).thenReturn(recurso);
        when(associacao.getPermissoes()).thenReturn(permissoes);

        assertSame(perfil, perfilService.findById(id));
        verify(recurso).getNome();
        verify(permissoes).size();
    }

    @Test
    void deveAceitarPerfilSemAssociacoesAoListar() {
        Perfil perfil = mock(Perfil.class);
        when(repository.findAll()).thenReturn(List.of(perfil));
        when(perfil.getPerfilRecursos()).thenReturn(null);

        assertEquals(List.of(perfil), perfilService.findAll());
    }

    @Test
    void deveCriarPerfilComEntidadesCarregadasEValoresNormalizados() {
        UUID recursoId = UUID.randomUUID();
        UUID permissaoId = UUID.randomUUID();
        Recurso recursoGerenciado = recurso(recursoId, true);
        Permissao permissaoGerenciada = permissao(permissaoId, true);
        Perfil perfil = perfilRecebido("  Gestor  ", recursoId, List.of(permissaoId));
        perfil.setAtivo(null);

        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(false);
        when(recursoService.findById(recursoId)).thenReturn(recursoGerenciado);
        when(permissaoService.findById(permissaoId)).thenReturn(permissaoGerenciada);
        when(repository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Perfil resultado = perfilService.create(perfil);

        assertEquals("Gestor", resultado.getNome());
        assertEquals("GESTOR", resultado.getChave());
        assertTrue(resultado.getAtivo());
        assertSame(recursoGerenciado, resultado.getPerfilRecursos().get(0).getRecurso());
        assertSame(permissaoGerenciada, resultado.getPerfilRecursos().get(0).getPermissoes().get(0));
        assertSame(resultado, resultado.getPerfilRecursos().get(0).getPerfil());
    }

    @Test
    void deveAceitarColecaoNulaComoPerfilSemRecursos() {
        Perfil perfil = Perfil.builder().nome("Gestor").perfilRecursos(null).build();
        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(false);
        when(repository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Perfil resultado = perfilService.create(perfil);

        assertTrue(resultado.getPerfilRecursos().isEmpty());
    }

    @Test
    void deveRejeitarNomeDuplicadoNaCriacaoEAtualizacao() {
        Perfil perfil = Perfil.builder().nome("Gestor").build();
        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(true);
        assertThrows(ConflictException.class, () -> perfilService.create(perfil));

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(Perfil.builder().id(id).nome("Atual").build()));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Gestor", id)).thenReturn(true);
        assertThrows(ConflictException.class, () -> perfilService.update(id, perfil));

        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarChaveEReconciliarRecursos() {
        UUID perfilId = UUID.randomUUID();
        UUID recursoMantidoId = UUID.randomUUID();
        UUID recursoRemovidoId = UUID.randomUUID();
        UUID recursoNovoId = UUID.randomUUID();
        UUID permissaoId = UUID.randomUUID();

        PerfilRecurso mantido = associacao(recursoMantidoId, List.of());
        PerfilRecurso removido = associacao(recursoRemovidoId, List.of());
        Perfil existente = Perfil.builder()
                .id(perfilId)
                .nome("Anterior")
                .chave("ANTERIOR")
                .ativo(true)
                .perfilRecursos(new ArrayList<>(List.of(mantido, removido)))
                .build();
        Perfil alteracoes = Perfil.builder()
                .nome("Novo nome")
                .ativo(null)
                .perfilRecursos(List.of(
                        associacao(recursoMantidoId, List.of(permissaoId)),
                        associacao(recursoNovoId, List.of(permissaoId))
                ))
                .build();

        when(repository.findById(perfilId)).thenReturn(Optional.of(existente));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Novo nome", perfilId)).thenReturn(false);
        when(recursoService.findById(recursoMantidoId)).thenReturn(recurso(recursoMantidoId, true));
        when(recursoService.findById(recursoNovoId)).thenReturn(recurso(recursoNovoId, true));
        when(permissaoService.findById(permissaoId)).thenReturn(permissao(permissaoId, true));
        when(repository.save(existente)).thenReturn(existente);

        Perfil resultado = perfilService.update(perfilId, alteracoes);

        assertEquals("NOVO NOME", resultado.getChave());
        assertTrue(resultado.getAtivo());
        assertEquals(2, resultado.getPerfilRecursos().size());
        assertTrue(resultado.getPerfilRecursos().contains(mantido));
        assertTrue(resultado.getPerfilRecursos().stream()
                .noneMatch(item -> item.getRecurso().getId().equals(recursoRemovidoId)));
        assertEquals(1, mantido.getPermissoes().size());
    }

    @Test
    void deveRejeitarRecursoOuPermissaoDuplicados() {
        UUID recursoId = UUID.randomUUID();
        UUID permissaoId = UUID.randomUUID();
        Perfil recursoDuplicado = Perfil.builder()
                .nome("Gestor")
                .perfilRecursos(List.of(
                        associacao(recursoId, List.of()),
                        associacao(recursoId, List.of())
                ))
                .build();
        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(false);
        when(recursoService.findById(recursoId)).thenReturn(recurso(recursoId, true));

        assertThrows(BusinessException.class, () -> perfilService.create(recursoDuplicado));

        Perfil permissaoDuplicada = perfilRecebido("Gestor", recursoId, List.of(permissaoId, permissaoId));
        when(permissaoService.findById(permissaoId)).thenReturn(permissao(permissaoId, true));
        assertThrows(BusinessException.class, () -> perfilService.create(permissaoDuplicada));
    }

    @Test
    void deveRejeitarRecursoOuPermissaoInativos() {
        UUID recursoId = UUID.randomUUID();
        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(false);
        when(recursoService.findById(recursoId)).thenReturn(recurso(recursoId, false));
        assertThrows(
                BusinessException.class,
                () -> perfilService.create(perfilRecebido("Gestor", recursoId, List.of()))
        );

        UUID permissaoId = UUID.randomUUID();
        when(recursoService.findById(recursoId)).thenReturn(recurso(recursoId, true));
        when(permissaoService.findById(permissaoId)).thenReturn(permissao(permissaoId, false));
        assertThrows(
                BusinessException.class,
                () -> perfilService.create(perfilRecebido("Gestor", recursoId, List.of(permissaoId)))
        );
    }

    @Test
    void devePropagarErroQuandoRecursoNaoExiste() {
        UUID recursoId = UUID.randomUUID();
        ResourceNotFoundException erro = new ResourceNotFoundException("Recurso não encontrado");
        when(repository.existsByNomeIgnoreCase("Gestor")).thenReturn(false);
        when(recursoService.findById(recursoId)).thenThrow(erro);

        ResourceNotFoundException resultado = assertThrows(
                ResourceNotFoundException.class,
                () -> perfilService.create(perfilRecebido("Gestor", recursoId, List.of()))
        );

        assertSame(erro, resultado);
    }

    private Perfil perfilRecebido(String nome, UUID recursoId, List<UUID> permissoes) {
        return Perfil.builder()
                .nome(nome)
                .ativo(true)
                .perfilRecursos(List.of(associacao(recursoId, permissoes)))
                .build();
    }

    private PerfilRecurso associacao(UUID recursoId, List<UUID> permissoes) {
        return PerfilRecurso.builder()
                .recurso(Recurso.builder().id(recursoId).build())
                .permissoes(permissoes.stream().map(id -> Permissao.builder().id(id).build()).toList())
                .build();
    }

    private Recurso recurso(UUID id, boolean ativo) {
        return Recurso.builder().id(id).nome("Recurso").chave("RECURSO").ativo(ativo).build();
    }

    private Permissao permissao(UUID id, boolean ativo) {
        return Permissao.builder().id(id).nome("Visualizar").chave("VIEW").ativo(ativo).build();
    }
}
