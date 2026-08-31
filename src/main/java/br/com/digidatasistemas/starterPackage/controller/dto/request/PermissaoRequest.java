package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class PermissaoRequest
        implements IRequest<PermissaoRequest, Permissao> {

    private UUID id;
    private String nome;
    private String chave;
    private String descricao;
    private Boolean ativo;

    @Override
    public Permissao to(PermissaoRequest request) {

        return Permissao.builder()
                .id(request.getId())
                .nome(request.getNome())
                .chave(request.getChave())
                .descricao(request.getDescricao())
                .ativo(request.getAtivo())
                .build();
    }

    @Override
    public List<Permissao> to(List<PermissaoRequest> requests) {
        return requests.stream()
                .map(this::to)
                .toList();
    }
}