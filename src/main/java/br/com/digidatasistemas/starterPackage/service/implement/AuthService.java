package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.controller.dto.request.LoginRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;
import br.com.digidatasistemas.starterPackage.exception.UnauthorizedException;
import br.com.digidatasistemas.starterPackage.model.User;
import br.com.digidatasistemas.starterPackage.repository.UserRepository;
import br.com.digidatasistemas.starterPackage.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.cpf(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new UnauthorizedException("Usuário ou senha inválidos");
        }

        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);

        Map<String, List<String>> resources = new HashMap<>();

        user.getProfile().getProfileResources().forEach(profileResource -> {
            String name = profileResource.getResource().getKey();
            List<String> permissions = new ArrayList<>();
            profileResource.getPermissions().forEach(permission -> {
               permissions.add(permission.getKey());
            });
            resources.put(name, permissions);
        });

        return new LoginResponse(
                token,
                "",
                user.getName(),
                user.getCpf(),
                resources
        );
    }
}