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

@Getter
@Setter
@Component
public class PermissaoRequest
        implements IRequest<PermissaoRequest, Permissao> {

    private UUID id;
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
    private String nome;
    @NotBlank(message = "Chave é obrigatória")
    @Size(max = 50, message = "Chave deve possuir no máximo 50 caracteres")
    @Pattern(
            regexp = "[A-Z][A-Z0-9_]*",
            message = "Chave deve conter apenas letras maiúsculas, números e sublinhado"
    )
    private String chave;
    @Size(max = 255, message = "Descrição deve possuir no máximo 255 caracteres")
    private String descricao;
    @NotNull(message = "Ativo é obrigatório")
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
