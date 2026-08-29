package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.ProfileResource;
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
public class ProfileResourceResponse implements IResponse<ProfileResource, ProfileResourceResponse> {

    private UUID id;
    private UUID resourceId;
    private String resource;
    private List<PermissionResponse> permission = new ArrayList<>();

    public ProfileResourceResponse(ProfileResource profileResource) {
        this.id = profileResource.getId();
        this.resourceId = profileResource.getResource().getId();
        this.resource = profileResource.getResource().getName();
        this.permission = profileResource.getPermissions().stream().map(PermissionResponse::new).toList();
    }

    @Override
    public ProfileResourceResponse to(ProfileResource profileResource) {
        return ProfileResourceResponse.builder()
                .id(profileResource.getId())
                .resourceId(profileResource.getResource().getId())
                .resource(profileResource.getResource().getName())
                .build();
    }

    @Override
    public List<ProfileResourceResponse> to(List<ProfileResource> profileResources) {
        return profileResources.stream().map(this::to).toList();
    }
}
