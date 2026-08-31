package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.RecursoRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.RecursoResponse;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.service.IRecursoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resource")
@RecursoPermissao("RECURSO")
public class RecursoController
        extends BaseCrudController<RecursoRequest, RecursoResponse, Recurso> {

    private final IRecursoService<Recurso> service;

    public RecursoController(
            IRecursoService<Recurso> service,
            IRequest<RecursoRequest, Recurso> request,
            IResponse<Recurso, RecursoResponse> response) {

        super(service, request, response);
        this.service = service;
    }
}