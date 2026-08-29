package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Profile;
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
public class ProfileResponse implements IResponse<Profile, ProfileResponse> {

    private UUID id;
    private String name;
    private String key;
    private String description;
    private Boolean active;
    private List<ProfileResourceResponse> profileResourceResponse = new ArrayList<>();

    public ProfileResponse(Profile profile) {
        this.id = profile.getId();
        this.name = profile.getName();
        this.key = profile.getKey();
        this.description = profile.getDescription();
        this.active = profile.getActive();
        this.profileResourceResponse = profile.getProfileResources().stream().map(ProfileResourceResponse::new).toList();
    }

    @Override
    public ProfileResponse to(Profile profile) {
        return new ProfileResponse(profile);
    }

    @Override
    public List<ProfileResponse> to(List<Profile> profiles) {
        return profiles.stream().map(this::to).toList();
    }
}
