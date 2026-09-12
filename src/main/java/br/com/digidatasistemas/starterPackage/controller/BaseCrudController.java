package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.CrudController;
import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidata.crud.service.ICrudService;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.service.IAutorizacaoService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.MSG_USUARIO_NAO_AUTENTICADO;
import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.MSG_USUARIO_SEM_PERMISSAO_ESPECIFICA;

public abstract class BaseCrudController<
        Request,
        Response,
        Model>
        extends CrudController<Request, Response, Model> {

    private final IAutorizacaoService autorizacaoService;

    protected BaseCrudController(
            ICrudService<Model, UUID> service,
            IRequest<Request, Model> request,
            IResponse<Model, Response> response,
            IAutorizacaoService autorizacaoService) {

        super(service, request, response);
        this.autorizacaoService = autorizacaoService;
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

        RecursoPermissao recursoPermissao =
                getClass().getAnnotation(
                        RecursoPermissao.class
                );

        if (recursoPermissao == null) {

            throw new IllegalStateException(
                    "O controller "
                            + getClass().getName()
                            + " não possui @RecursoPermissao"
            );
        }

        String resource =
                recursoPermissao.value();

        String requiredAuthority =
                resource + ":" + permission;

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    MSG_USUARIO_NAO_AUTENTICADO
            );
        }

        if (!autorizacaoService.hasPermission(authentication, resource, permission)) {

            throw new AccessDeniedException(
                    MSG_USUARIO_SEM_PERMISSAO_ESPECIFICA.formatted(requiredAuthority)
            );
        }
    }
}
