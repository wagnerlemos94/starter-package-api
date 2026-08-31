package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Recurso;
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
public class RecursoResponse implements IResponse<Recurso, RecursoResponse> {

    private UUID id;
    private String nome;
    private String chave;
    private String descricao;
    private Boolean ativo;

    @Override
    public RecursoResponse to(Recurso recurso) {

        return RecursoResponse.builder()
                .id(recurso.getId())
                .nome(recurso.getNome())
                .chave(recurso.getChave())
                .descricao(recurso.getDescricao())
                .ativo(recurso.getAtivo())
                .build();
    }

    @Override
    public List<RecursoResponse> to(List<Recurso> recursos) {
        return recursos.stream()
                .map(this::to)
                .toList();
    }
}