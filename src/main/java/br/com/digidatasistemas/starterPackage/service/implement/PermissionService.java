package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Permission;
import br.com.digidatasistemas.starterPackage.repository.PermissionRepository;
import br.com.digidatasistemas.starterPackage.service.IPermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService extends CrudService<Permission, UUID> implements IPermissionService<Permission> {

    private PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
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