package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidata.crud.controller.dto.request.IRequest;
import br.com.digidata.crud.controller.dto.response.IResponse;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.controller.dto.request.UsuarioRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.request.UsuarioContaRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.UsuarioResponse;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import br.com.digidatasistemas.starterPackage.service.IAutorizacaoService;
import br.com.digidatasistemas.starterPackage.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RecursoPermissao("USUARIO")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController extends BaseCrudController<UsuarioRequest, UsuarioResponse, Usuario>{

    private final IUsuarioService<Usuario> service;
    private final IResponse<Usuario, UsuarioResponse> response;
    private final UsuarioAutenticado usuarioAutenticado;

    public UsuarioController(IUsuarioService<Usuario> service, IRequest<UsuarioRequest, Usuario> request,
                             IResponse<Usuario, UsuarioResponse> response, IAutorizacaoService autorizacaoService,
                             UsuarioAutenticado usuarioAutenticado) {
        super(service, request, response, autorizacaoService);
        this.service = service;
        this.response = response;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @GetMapping("/me")
    public UsuarioResponse current() {
        return response.to(service.findById(usuarioAutenticado.getId()));
    }

    @PutMapping("/me")
    public UsuarioResponse updateCurrent(@Valid @RequestBody UsuarioContaRequest request) {
        return response.to(service.updateCurrent(usuarioAutenticado.getId(), request.name(),
                request.password(), request.currentPassword()));
    }

}
