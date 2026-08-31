package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.repository.PermissaoRepository;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissaoService extends CrudService<Permissao, UUID> implements IPermissaoService<Permissao> {

    private PermissaoRepository repository;

    public PermissaoService(PermissaoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public boolean hasPermission(
            Authentication authentication,
            String resource,
            String permission) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return false;
        }

        String requiredAuthority =
                resource + ":" + permission;

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority
                                .getAuthority()
                                .equals(requiredAuthority)
                );
    }
}