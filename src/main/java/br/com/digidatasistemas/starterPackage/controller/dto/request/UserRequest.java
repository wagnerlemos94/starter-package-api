package br.com.digidatasistemas.starterPackage.controller.dto.request;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidatasistemas.starterPackage.model.Profile;
import br.com.digidatasistemas.starterPackage.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
public class UserRequest implements IRequest<UserRequest, User> {

    private UUID id;
    private String cpf;
    private String name;
    private UUID profileId;
    private Boolean active;

    @Override
    public User to(UserRequest userRequest) {
        Profile profile = new Profile();
        profile.setId(userRequest.getProfileId());
        return User.builder().id(userRequest.getId()).cpf(userRequest.getCpf()).name(userRequest.getName()).active(userRequest.getActive()).profile(profile).build();
    }

    @Override
    public List<User> to(List<UserRequest> userRequests) {
        return userRequests.stream().map(this::to).toList();
    }

}
