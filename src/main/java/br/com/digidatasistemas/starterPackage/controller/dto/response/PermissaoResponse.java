package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PermissaoResponse
        implements IResponse<Permissao, PermissaoResponse> {

    private UUID id;
    private String nome;
    private String chave;
    private String descricao;
    private Boolean ativo;

    public PermissaoResponse(Permissao permissao) {
        this.id = permissao.getId();
        this.nome = permissao.getNome();
        this.chave = permissao.getChave();
        this.descricao = permissao.getDescricao();
        this.ativo = permissao.getAtivo();
    }

    @Override
    public PermissaoResponse to(Permissao permissao) {
        return new PermissaoResponse(permissao);
    }

    @Override
    public List<PermissaoResponse> to(List<Permissao> permissaos) {
        return permissaos.stream()
                .map(this::to)
                .toList();
    }
}