package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PerfilResponse implements IResponse<Perfil, PerfilResponse> {

    private UUID id;
    private String nome;
    private String chave;
    private String descricao;
    private Boolean ativo;
    @Builder.Default
    private List<PerfilRecursoResponse> perfilRecursoResponse = new ArrayList<>();

    public PerfilResponse(Perfil perfil) {
        this.id = perfil.getId();
        this.nome = perfil.getNome();
        this.chave = perfil.getChave();
        this.descricao = perfil.getDescricao();
        this.ativo = perfil.getAtivo();
        this.perfilRecursoResponse = perfil.getPerfilRecursos() == null
                ? List.of()
                : perfil.getPerfilRecursos().stream().map(PerfilRecursoResponse::new).toList();
    }

    @Override
    public PerfilResponse to(Perfil perfil) {
        return new PerfilResponse(perfil);
    }

    @Override
    public List<PerfilResponse> to(List<Perfil> perfils) {
        return perfils.stream().map(this::to).toList();
    }
}
