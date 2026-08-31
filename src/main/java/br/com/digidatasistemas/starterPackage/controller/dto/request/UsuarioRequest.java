package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class UsuarioRequest implements IRequest<UsuarioRequest, Usuario> {

    private UUID id;
    private String cpf;
    private String name;
    private UUID profileId;
    private Boolean active;

    @Override
    public Usuario to(UsuarioRequest usuarioRequest) {
        Perfil perfil = new Perfil();
        perfil.setId(usuarioRequest.getProfileId());
        return Usuario.builder().id(usuarioRequest.getId()).cpf(usuarioRequest.getCpf()).name(usuarioRequest.getName()).active(usuarioRequest.getActive()).perfil(perfil).build();
    }

    @Override
    public List<Usuario> to(List<UsuarioRequest> usuarioRequests) {
        return usuarioRequests.stream().map(this::to).toList();
    }

}
