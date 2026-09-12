package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.exception.ConflictException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.repository.PerfilRepository;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import br.com.digidatasistemas.starterPackage.service.IRecursoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

@Service
public class PerfilService extends CrudService<Perfil, UUID> implements IPerfilService<Perfil> {

    private final PerfilRepository repository;
    private final IRecursoService<Recurso> recursoService;
    private final IPermissaoService<Permissao> permissaoService;

    public PerfilService(
            PerfilRepository repository,
            IRecursoService<Recurso> recursoService,
            IPermissaoService<Permissao> permissaoService
    ) {
        super(repository);
        this.repository = repository;
        this.recursoService = recursoService;
        this.permissaoService = permissaoService;
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil findById(UUID id) {
        return inicializarRelacionamentos(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> findAll() {
        return super.findAll().stream()
                .map(this::inicializarRelacionamentos)
                .toList();
    }

    @Override
    @Transactional
    public Perfil create(Perfil perfil) {
        validarPerfil(perfil);
        String nome = perfil.getNome().trim();

        if (repository.existsByNomeIgnoreCase(nome)) {
            throw conflitoNome(nome);
        }

        perfil.setNome(nome);
        perfil.setChave(gerarChave(nome));
        perfil.setAtivo(perfil.getAtivo() != null ? perfil.getAtivo() : Boolean.TRUE);
        perfil.setPerfilRecursos(montarNovasAssociacoes(perfil, perfil.getPerfilRecursos()));

        return repository.save(perfil);
    }

    @Override
    @Transactional
    public Perfil update(UUID id, Perfil perfil) {
        validarPerfil(perfil);
        Perfil perfilSalvo = super.findById(id);
        String nome = perfil.getNome().trim();

        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw conflitoNome(nome);
        }

        perfilSalvo.setNome(nome);
        perfilSalvo.setChave(gerarChave(nome));
        perfilSalvo.setDescricao(perfil.getDescricao());
        if (perfil.getAtivo() != null) {
            perfilSalvo.setAtivo(perfil.getAtivo());
        }

        atualizarAssociacoes(perfilSalvo, perfil.getPerfilRecursos());
        return repository.save(perfilSalvo);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private List<PerfilRecurso> montarNovasAssociacoes(
            Perfil perfil,
            List<PerfilRecurso> associacoesRecebidas
    ) {
        Map<UUID, AssociacaoCarregada> associacoes = carregarAssociacoes(associacoesRecebidas);
        List<PerfilRecurso> resultado = new ArrayList<>();

        associacoes.forEach((recursoId, associacao) -> resultado.add(
                PerfilRecurso.builder()
                        .perfil(perfil)
                        .recurso(associacao.recurso())
                        .permissoes(associacao.permissoes())
                        .build()
        ));

        return resultado;
    }

    private void atualizarAssociacoes(Perfil perfil, List<PerfilRecurso> associacoesRecebidas) {
        Map<UUID, AssociacaoCarregada> associacoes = carregarAssociacoes(associacoesRecebidas);
        List<PerfilRecurso> atuais = perfil.getPerfilRecursos();

        if (atuais == null) {
            atuais = new ArrayList<>();
            perfil.setPerfilRecursos(atuais);
        }

        Map<UUID, PerfilRecurso> atuaisPorRecurso = new HashMap<>();
        for (PerfilRecurso atual : atuais) {
            atuaisPorRecurso.put(atual.getRecurso().getId(), atual);
        }

        atuais.removeIf(atual -> !associacoes.containsKey(atual.getRecurso().getId()));

        for (Map.Entry<UUID, AssociacaoCarregada> entry : associacoes.entrySet()) {
            UUID recursoId = entry.getKey();
            PerfilRecurso atual = atuaisPorRecurso.get(recursoId);

            if (atual == null) {
                atuais.add(PerfilRecurso.builder()
                        .perfil(perfil)
                        .recurso(entry.getValue().recurso())
                        .permissoes(entry.getValue().permissoes())
                        .build());
            } else {
                atual.setRecurso(entry.getValue().recurso());
                atual.setPermissoes(entry.getValue().permissoes());
            }
        }
    }

    private Map<UUID, AssociacaoCarregada> carregarAssociacoes(List<PerfilRecurso> recebidas) {
        List<PerfilRecurso> associacoes = recebidas == null ? List.of() : recebidas;
        Map<UUID, AssociacaoCarregada> resultado = new java.util.LinkedHashMap<>();
        Set<UUID> recursosEncontrados = new HashSet<>();

        for (PerfilRecurso associacao : associacoes) {
            if (associacao == null || associacao.getRecurso() == null || associacao.getRecurso().getId() == null) {
                throw new BusinessException(MSG_PERFIL_RECURSO_OBRIGATORIO);
            }

            UUID recursoId = associacao.getRecurso().getId();
            if (!recursosEncontrados.add(recursoId)) {
                throw new BusinessException(MSG_PERFIL_RECURSO_DUPLICADO);
            }

            Recurso recurso = recursoService.findById(recursoId);
            if (!Boolean.TRUE.equals(recurso.getAtivo())) {
                throw new BusinessException(MSG_PERFIL_RECURSO_INATIVO);
            }

            List<Permissao> permissoes = carregarPermissoes(associacao.getPermissoes());
            resultado.put(recursoId, new AssociacaoCarregada(recurso, permissoes));
        }

        return resultado;
    }

    private List<Permissao> carregarPermissoes(List<Permissao> recebidas) {
        List<Permissao> permissoes = recebidas == null ? List.of() : recebidas;
        List<Permissao> resultado = new ArrayList<>();
        Set<UUID> idsEncontrados = new HashSet<>();

        for (Permissao recebida : permissoes) {
            if (recebida == null || recebida.getId() == null) {
                throw new BusinessException(MSG_PERFIL_PERMISSAO_OBRIGATORIA);
            }
            if (!idsEncontrados.add(recebida.getId())) {
                throw new BusinessException(MSG_PERFIL_PERMISSAO_DUPLICADA);
            }

            Permissao permissao = permissaoService.findById(recebida.getId());
            if (!Boolean.TRUE.equals(permissao.getAtivo())) {
                throw new BusinessException(MSG_PERFIL_PERMISSAO_INATIVA);
            }
            resultado.add(permissao);
        }

        return resultado;
    }

    private void validarPerfil(Perfil perfil) {
        if (perfil == null || perfil.getNome() == null || perfil.getNome().isBlank()) {
            throw new BusinessException(MSG_NOME_OBRIGATORIO);
        }
    }

    private String gerarChave(String nome) {
        return nome.toUpperCase(Locale.ROOT);
    }

    private ConflictException conflitoNome(String nome) {
        return new ConflictException(MSG_PERFIL_NOME_EXISTENTE.formatted(nome));
    }

    private Perfil inicializarRelacionamentos(Perfil perfil) {
        List<PerfilRecurso> associacoes = perfil.getPerfilRecursos();
        if (associacoes == null) {
            return perfil;
        }

        associacoes.forEach(associacao -> {
            associacao.getRecurso().getNome();
            associacao.getPermissoes().size();
        });
        return perfil;
    }

    private record AssociacaoCarregada(Recurso recurso, List<Permissao> permissoes) {
    }
}
