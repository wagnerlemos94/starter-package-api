package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class RecursoRequest implements IRequest<RecursoRequest, Recurso> {

    private UUID id;
    private String nome;
    private String descricao;
    private Boolean ativo;

    @Override
    public Recurso to(RecursoRequest request) {

        return Recurso.builder()
                .id(request.getId())
                .nome(request.getNome())
                .chave(request.getNome().toUpperCase())
                .descricao(request.getDescricao())
                .ativo(request.getAtivo())
                .build();
    }

    @Override
    public List<Recurso> to(List<RecursoRequest> requests) {
        return requests.stream()
                .map(this::to)
                .toList();
    }
}