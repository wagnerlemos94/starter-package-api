package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.PermissaoRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.PermissaoResponse;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("permissao")
@RecursoPermissao("PERMISSOES")
@Tag(name = "Permissões", description = "Gerenciamento das operações permitidas")
public class PermissaoController
        extends BaseCrudController<PermissaoRequest, PermissaoResponse, Permissao> {

    private final IPermissaoService<Permissao> service;

    public PermissaoController(
            IPermissaoService<Permissao> service,
            IRequest<PermissaoRequest, Permissao> request,
            IResponse<Permissao, PermissaoResponse> response) {

        super(service, request, response);
        this.service = service;
    }
}
