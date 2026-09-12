package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

@Getter
@Setter
@Component
public class RecursoRequest implements IRequest<RecursoRequest, Recurso> {

    private UUID id;
    @NotBlank(message = MSG_NOME_OBRIGATORIO)
    @Size(max = 100, message = MSG_NOME_MAXIMO_100)
    private String nome;
    @Size(max = 255, message = MSG_DESCRICAO_MAXIMO_255)
    private String descricao;
    @NotNull(message = MSG_ATIVO_OBRIGATORIO)
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
