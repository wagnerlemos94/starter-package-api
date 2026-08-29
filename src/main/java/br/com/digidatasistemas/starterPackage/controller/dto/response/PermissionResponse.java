package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Permission;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PermissionResponse
        implements IResponse<Permission, PermissionResponse> {

    private UUID id;
    private String name;
    private String key;
    private String description;
    private Boolean active;

    public PermissionResponse(Permission permission) {
        this.id = permission.getId();
        this.name = permission.getName();
        this.key = permission.getKey();
        this.description = permission.getDescription();
        this.active = permission.getActive();
    }

    @Override
    public PermissionResponse to(Permission permission) {
        return new PermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> to(List<Permission> permissions) {
        return permissions.stream()
                .map(this::to)
                .toList();
    }
}