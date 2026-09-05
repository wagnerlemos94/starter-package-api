package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.PerfilRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.PerfilResponse;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("perfil")
@RecursoPermissao("PERFIL")
@Tag(name = "Perfis", description = "Gerenciamento de perfis e suas permissões")
public class PerfilController extends BaseCrudController<PerfilRequest, PerfilResponse, Perfil> {

    private IPerfilService<Perfil> service;

    public PerfilController(IPerfilService<Perfil> service, IRequest<PerfilRequest, Perfil> request, IResponse<Perfil, PerfilResponse> response) {
        super(service, request, response);
        this.service = service;
    }

}
