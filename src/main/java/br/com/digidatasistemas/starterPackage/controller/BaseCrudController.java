package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.CrudController;
import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidata.crud.service.ICrudService;
import br.com.digidatasistemas.starterPackage.security.permission.ResourcePermission;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public abstract class BaseCrudController<
        Request,
        Response,
        Model>
        extends CrudController<Request, Response, Model> {

    protected BaseCrudController(
            ICrudService<Model, UUID> service,
            IRequest<Request, Model> request,
            IResponse<Model, Response> response) {

        super(service, request, response);
    }

    @Override
    public Response create(Request request) {

        checkCrudPermission("CREATE");

        return super.create(request);
    }

    @Override
    public Response update(
            Request request,
            UUID id) {

        checkCrudPermission("UPDATE");

        return super.update(request, id);
    }

    @Override
    public List<Response> list() {

        checkCrudPermission("VIEW");

        return super.list();
    }

    @Override
    public Response findById(UUID id) {

        checkCrudPermission("VIEW");

        return super.findById(id);
    }

    @Override
    public void delete(UUID id) {

        checkCrudPermission("DELETE");

        super.delete(id);
    }

    protected void checkCrudPermission(
            String permission) {

        ResourcePermission resourcePermission =
                getClass().getAnnotation(
                        ResourcePermission.class
                );

        if (resourcePermission == null) {

            throw new IllegalStateException(
                    "O controller "
                            + getClass().getName()
                            + " não possui @ResourcePermission"
            );
        }

        String resource =
                resourcePermission.value();

        String requiredAuthority =
                resource + ":" + permission;

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Usuário não autenticado."
            );
        }

        boolean hasPermission =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority
                                        .getAuthority()
                                        .equals(requiredAuthority)
                        );

        if (!hasPermission) {

            throw new AccessDeniedException(
                    "Usuário não possui a permissão: "
                            + requiredAuthority
            );
        }
    }
}