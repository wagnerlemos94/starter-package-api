package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Permission;
import br.com.digidatasistemas.starterPackage.model.Profile;
import br.com.digidatasistemas.starterPackage.model.ProfileResource;
import br.com.digidatasistemas.starterPackage.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class ProfileRequest implements IRequest<ProfileRequest, Profile> {

    private UUID id;
    private String name;
    private String description;
    private Boolean active = Boolean.TRUE;

    private Map<UUID, List<UUID>> profilesResource = new HashMap<>();

    @Override
    public Profile to(ProfileRequest profileRequest) {

        Profile profile = Profile.builder()
                .id(profileRequest.getId())
                .name(profileRequest.getName())
                .key(profileRequest.getName().toUpperCase())
                .description(profileRequest.getDescription())
                .active(profileRequest.getActive())
                .build();

        List<ProfileResource> profileResources = new ArrayList<>();

        profileRequest.getProfilesResource().forEach((resourceId, permissionIds) -> {

            Resource resource = new Resource();
            resource.setId(resourceId);

            List<Permission> permissions = permissionIds.stream()
                    .map(permissionId -> {
                        Permission permission = new Permission();
                        permission.setId(permissionId);
                        return permission;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            ProfileResource profileResource = new ProfileResource();

            profileResource.setProfile(profile);
            profileResource.setResource(resource);
            profileResource.setPermissions(permissions);

            profileResources.add(profileResource);
        });

        profile.setProfileResources(profileResources);

        return profile;
    }

    @Override
    public List<Profile> to(List<ProfileRequest> profileRequests) {
        return profileRequests.stream()
                .map(this::to)
                .toList();
    }
}