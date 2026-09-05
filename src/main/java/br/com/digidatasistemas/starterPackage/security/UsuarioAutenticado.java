package br.com.digidatasistemas.starterPackage.security;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_USUARIO_NAO_AUTENTICADO;

@Component
public class UsuarioAutenticado {

    public Usuario get() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Usuario usuario)) {

            throw new AccessDeniedException(MSG_USUARIO_NAO_AUTENTICADO);
        }

        return usuario;
    }

    public UUID getId() {
        return get().getId();
    }

    public String getCpf() {
        return get().getCpf();
    }
}
