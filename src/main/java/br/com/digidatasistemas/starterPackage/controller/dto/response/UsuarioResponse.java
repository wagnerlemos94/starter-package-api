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
    private String nome;
    private Boolean ativo;
    private String perfil;
    private UUID perfilId;

    @Override
    public UsuarioResponse to(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .cpf(usuario.getCpf())
                .nome(usuario.getNome())
                .ativo(usuario.getAtivo())
                .perfil(usuario.getPerfil().getNome())
                .perfilId(usuario.getPerfil().getId())
                .build();
    }

    @Override
    public List<UsuarioResponse> to(List<Usuario> usuarios) {
        return usuarios.stream().map(this::to).toList();
    }
}
