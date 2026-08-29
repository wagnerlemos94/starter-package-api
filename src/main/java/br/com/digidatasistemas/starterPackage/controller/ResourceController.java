package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.ResourceRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.ResourceResponse;
import br.com.digidatasistemas.starterPackage.model.Resource;
import br.com.digidatasistemas.starterPackage.security.permission.ResourcePermission;
import br.com.digidatasistemas.starterPackage.service.IResourceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resource")
@ResourcePermission("RECURSO")
public class ResourceController
        extends BaseCrudController<ResourceRequest, ResourceResponse, Resource> {

    private final IResourceService<Resource> service;

    public ResourceController(
            IResourceService<Resource> service,
            IRequest<ResourceRequest, Resource> request,
            IResponse<Resource, ResourceResponse> response) {

        super(service, request, response);
        this.service = service;
    }
}