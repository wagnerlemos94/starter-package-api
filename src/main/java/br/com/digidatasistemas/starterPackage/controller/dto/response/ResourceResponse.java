package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Resource;
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
public class ResourceResponse implements IResponse<Resource, ResourceResponse> {

    private UUID id;
    private String name;
    private String key;
    private String description;
    private Boolean active;

    @Override
    public ResourceResponse to(Resource resource) {

        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .key(resource.getKey())
                .description(resource.getDescription())
                .active(resource.getActive())
                .build();
    }

    @Override
    public List<ResourceResponse> to(List<Resource> resources) {
        return resources.stream()
                .map(this::to)
                .toList();
    }
}