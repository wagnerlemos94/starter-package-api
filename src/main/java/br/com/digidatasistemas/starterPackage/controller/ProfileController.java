package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.ProfileRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.ProfileResponse;
import br.com.digidatasistemas.starterPackage.model.Profile;
import br.com.digidatasistemas.starterPackage.security.permission.ResourcePermission;
import br.com.digidatasistemas.starterPackage.service.IProfileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("profile")
@ResourcePermission("PERFIL")
public class ProfileController extends BaseCrudController<ProfileRequest, ProfileResponse, Profile> {

    private IProfileService<Profile> service;

    public ProfileController(IProfileService<Profile> service, IRequest<ProfileRequest, Profile> request, IResponse<Profile, ProfileResponse> response) {
        super(service, request, response);
        this.service = service;
    }

}
