package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PerfilRecursoResponse implements IResponse<PerfilRecurso, PerfilRecursoResponse> {

    private UUID id;
    private UUID recursoId;
    private String recurso;
    private List<PermissaoResponse> permissoes = new ArrayList<>();

    public PerfilRecursoResponse(PerfilRecurso perfilRecurso) {
        this.id = perfilRecurso.getId();
        this.recursoId = perfilRecurso.getRecurso().getId();
        this.recurso = perfilRecurso.getRecurso().getNome();
        this.permissoes = perfilRecurso.getPermissoes().stream().map(PermissaoResponse::new).toList();
    }

    @Override
    public PerfilRecursoResponse to(PerfilRecurso perfilRecurso) {
        return PerfilRecursoResponse.builder()
                .id(perfilRecurso.getId())
                .recursoId(perfilRecurso.getRecurso().getId())
                .recurso(perfilRecurso.getRecurso().getNome())
                .build();
    }

    @Override
    public List<PerfilRecursoResponse> to(List<PerfilRecurso> perfilRecursos) {
        return perfilRecursos.stream().map(this::to).toList();
    }
}
