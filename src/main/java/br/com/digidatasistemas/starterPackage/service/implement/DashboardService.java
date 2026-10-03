package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.service.*;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DashboardService implements IDashboardService {

    private final IUsuarioService<Usuario> usuarioService;
    private final IPerfilService<Perfil> perfilService;
    private final IPermissaoService<Permissao> permissaoService;
    private final IRecursoService<Recurso> recursoService;


    public List<DashboardResponse> listar() {
        return List.of(
                usuarioService.getDashboardData(),
                perfilService.getDashboardData(),
                recursoService.getDashboardData(),
                permissaoService.getDashboardData())
                ;
    }

}
