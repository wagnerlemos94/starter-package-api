package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Permission;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class PermissionRequest
        implements IRequest<PermissionRequest, Permission> {

    private UUID id;
    private String name;
    private String key;
    private String description;
    private Boolean active;

    @Override
    public Permission to(PermissionRequest request) {

        return Permission.builder()
                .id(request.getId())
                .name(request.getName())
                .key(request.getKey())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
    }

    @Override
    public List<Permission> to(List<PermissionRequest> requests) {
        return requests.stream()
                .map(this::to)
                .toList();
    }
}