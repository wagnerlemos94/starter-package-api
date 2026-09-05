package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_RECURSO_OBRIGATORIO;

@Getter
@Setter
@Component
public class UsuarioRequest implements IRequest<UsuarioRequest, Usuario> {

    private UUID id;
    @NotBlank(message = "cpf é " + MSG_RECURSO_OBRIGATORIO)
    @Pattern(
            regexp = "\\d{11}",
            message = "CPF deve conter exatamente 11 números"
    )
    private String cpf;
    @NotBlank(message = "Nome é " + MSG_RECURSO_OBRIGATORIO)
    @Size(max = 150, message = "Nome deve possuir no máximo 150 caracteres")
    private String name;
    @NotNull(message = "Perfil é " + MSG_RECURSO_OBRIGATORIO)
    private UUID profileId;
    @Size(min = 8, max = 72)
    private String password;
    private Boolean active;

    @Override
    public Usuario to(UsuarioRequest usuarioRequest) {
        Perfil perfil = new Perfil();
        perfil.setId(usuarioRequest.getProfileId());
        return Usuario.builder()
                .id(usuarioRequest.getId())
                .cpf(usuarioRequest.getCpf())
                .name(usuarioRequest.getName())
                .active(usuarioRequest.getActive())
                .perfil(perfil)
                .password(usuarioRequest.getPassword())
                .build();
    }

    @Override
    public List<Usuario> to(List<UsuarioRequest> usuarioRequests) {
        return usuarioRequests.stream().map(this::to).toList();
    }

}
