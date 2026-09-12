package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

@Getter
@Setter
@Component
public class PerfilRequest implements IRequest<PerfilRequest, Perfil> {

    private UUID id;
    @NotBlank(message = MSG_NOME_OBRIGATORIO)
    @Size(max = 100, message = MSG_NOME_MAXIMO_100)
    private String nome;
    @Size(max = 255, message = MSG_DESCRICAO_MAXIMO_255)
    private String descricao;
    private Boolean ativo = Boolean.TRUE;

    private Map<UUID, List<UUID>> perfilRecurso = new HashMap<>();

    @Override
    public Perfil to(PerfilRequest perfilRequest) {

        Perfil perfil = Perfil.builder()
                .id(perfilRequest.getId())
                .nome(perfilRequest.getNome())
                .descricao(perfilRequest.getDescricao())
                .ativo(perfilRequest.getAtivo())
                .build();

        List<PerfilRecurso> perfilRecursos = new ArrayList<>();

        Map<UUID, List<UUID>> associacoes = perfilRequest.getPerfilRecurso() == null
                ? Collections.emptyMap()
                : perfilRequest.getPerfilRecurso();

        associacoes.forEach((resourceId, permissionIds) -> {

            Recurso recurso = new Recurso();
            recurso.setId(resourceId);

            List<Permissao> permissaos = (permissionIds == null ? List.<UUID>of() : permissionIds).stream()
                    .map(permissionId -> {
                        Permissao permissao = new Permissao();
                        permissao.setId(permissionId);
                        return permissao;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            PerfilRecurso perfilRecurso = new PerfilRecurso();

            perfilRecurso.setPerfil(perfil);
            perfilRecurso.setRecurso(recurso);
            perfilRecurso.setPermissoes(permissaos);

            perfilRecursos.add(perfilRecurso);
        });

        perfil.setPerfilRecursos(perfilRecursos);

        return perfil;
    }

    @Override
    public List<Perfil> to(List<PerfilRequest> perfilRequests) {
        return perfilRequests.stream()
                .map(this::to)
                .toList();
    }
}
