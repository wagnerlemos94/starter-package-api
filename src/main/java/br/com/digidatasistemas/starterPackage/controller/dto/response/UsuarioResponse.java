package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse implements IResponse<Usuario, UsuarioResponse> {

    private UUID id;
    private String cpf;
    private String name;
    private Boolean active;
    private String profile;
    private UUID profileId;

    @Override
    public UsuarioResponse to(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .cpf(usuario.getCpf())
                .name(usuario.getName())
                .active(usuario.getActive())
                .profile(usuario.getPerfil().getNome())
                .profileId(usuario.getPerfil().getId())
                .build();
    }

    @Override
    public List<UsuarioResponse> to(List<Usuario> usuarios) {
        return usuarios.stream().map(this::to).toList();
    }
}
