package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.controller.dto.request.PermissionRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.PermissionResponse;
import br.com.digidatasistemas.starterPackage.model.Permission;
import br.com.digidatasistemas.starterPackage.security.permission.ResourcePermission;
import br.com.digidatasistemas.starterPackage.service.IPermissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("permission")
@ResourcePermission("PERMISSOES")
public class PermissionController
        extends BaseCrudController<PermissionRequest, PermissionResponse, Permission> {

    private final IPermissionService<Permission> service;

    public PermissionController(
            IPermissionService<Permission> service,
            IRequest<PermissionRequest, Permission> request,
            IResponse<Permission, PermissionResponse> response) {

        super(service, request, response);
        this.service = service;
    }
}