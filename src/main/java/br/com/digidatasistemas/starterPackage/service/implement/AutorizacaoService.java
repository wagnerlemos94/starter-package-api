package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.service.IAutorizacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService implements IAutorizacaoService {

    @Override
    public boolean hasPermission(
            Authentication authentication,
            String resource,
            String permission) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String requiredAuthority = resource + ":" + permission;

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredAuthority));
    }
}
