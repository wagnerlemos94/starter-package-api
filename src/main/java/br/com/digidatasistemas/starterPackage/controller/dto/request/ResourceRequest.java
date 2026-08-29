package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class ResourceRequest implements IRequest<ResourceRequest, Resource> {

    private UUID id;
    private String name;
    private String description;
    private Boolean active;

    @Override
    public Resource to(ResourceRequest request) {

        return Resource.builder()
                .id(request.getId())
                .name(request.getName())
                .key(request.getName().toUpperCase())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
    }

    @Override
    public List<Resource> to(List<ResourceRequest> requests) {
        return requests.stream()
                .map(this::to)
                .toList();
    }
}