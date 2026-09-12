package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class PermissaoRequest
        implements IRequest<PermissaoRequest, Permissao> {

    private UUID id;
    @NotBlank(message = MSG_NOME_OBRIGATORIO)
    @Size(max = 100, message = MSG_NOME_MAXIMO_100)
    private String nome;
    @NotBlank(message = MSG_CHAVE_OBRIGATORIA)
    @Size(max = 50, message = MSG_CHAVE_MAXIMO_50)
    @Pattern(
            regexp = "[A-Z][A-Z0-9_]*",
            message = MSG_CHAVE_FORMATO_INVALIDO
    )
    private String chave;
    @Size(max = 255, message = MSG_DESCRICAO_MAXIMO_255)
    private String descricao;
    @NotNull(message = MSG_ATIVO_OBRIGATORIO)
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
