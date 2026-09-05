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
    private PerfilResponse profile;

    @Override
    public UsuarioResponse to(Usuario usuario) {
        PerfilResponse profile =  usuario.getPerfil() != null ? new PerfilResponse(usuario.getPerfil()) : new PerfilResponse();
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .cpf(usuario.getCpf())
                .name(usuario.getName())
                .active(usuario.getActive())
                .profile(profile)
                .build();
    }

    @Override
    public List<UsuarioResponse> to(List<Usuario> usuarios) {
        return usuarios.stream().map(this::to).toList();
    }
}
