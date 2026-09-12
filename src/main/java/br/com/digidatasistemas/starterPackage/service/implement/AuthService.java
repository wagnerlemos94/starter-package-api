package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.controller.dto.request.LoginRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;
import br.com.digidatasistemas.starterPackage.exception.UnauthorizedException;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.service.IAuthService;
import br.com.digidatasistemas.starterPackage.service.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_USUARIO_INATIVO;
import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_USUARIO_OU_SENHA_INVALIDOS;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.cpf(),
                            request.password()
                    )
            );
        } catch (DisabledException e) {
            throw new UnauthorizedException(MSG_USUARIO_INATIVO);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(MSG_USUARIO_OU_SENHA_INVALIDOS);
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = jwtService.generateToken(usuario);

        Map<String, List<String>> resources = new HashMap<>();

        usuario.getPerfil().getPerfilRecursos().forEach(profileResource -> {
            String name = profileResource.getRecurso().getChave();
            List<String> permissions = new ArrayList<>();
            profileResource.getPermissoes().forEach(permission -> {
               permissions.add(permission.getChave());
            });
            resources.put(name, permissions);
        });

        return new LoginResponse(
                token,
                jwtService.getExpirationMillis(),
                usuario.getName(),
                usuario.getCpf(),
                resources
        );
    }
}
