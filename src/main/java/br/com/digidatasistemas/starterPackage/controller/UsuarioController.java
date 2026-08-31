package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.controller.dto.request.UsuarioRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.UsuarioResponse;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RecursoPermissao("USUARIO")
public class UsuarioController extends BaseCrudController<UsuarioRequest, UsuarioResponse, Usuario>{

    public UsuarioController(IUsuarioService<Usuario> service, IRequest<UsuarioRequest, Usuario> request, IResponse<Usuario, UsuarioResponse> response) {
        super(service, request, response);
    }

}
