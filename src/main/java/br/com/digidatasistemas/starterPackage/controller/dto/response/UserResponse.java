package br.com.digidatasistemas.starterPackage.controller.dto.response;

import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserResponse implements IResponse<User, UserResponse> {

    private UUID id;
    private String cpf;
    @JsonIgnore
    private String password;
    private String name;
    private Boolean active;
    private ProfileResponse profile;

    @Override
    public UserResponse to(User user) {
        ProfileResponse profile =  user.getProfile() != null ? new ProfileResponse(user.getProfile()) : new ProfileResponse();
        return UserResponse.builder()
                .id(user.getId())
                .cpf(user.getCpf())
                .name(user.getName())
                .active(user.getActive())
                .password(user.getPassword())
                .profile(profile)
                .build();
    }

    @Override
    public List<UserResponse> to(List<User> users) {
        return users.stream().map(this::to).toList();
    }
}
