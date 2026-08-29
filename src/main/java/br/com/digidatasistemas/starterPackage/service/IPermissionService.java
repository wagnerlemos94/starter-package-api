package br.com.digidatasistemas.starterPackage.service;

import br.com.digidata.crud.service.ICrudService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface IPermissionService<T> extends ICrudService<T, UUID> {

    boolean hasPermission(
            Authentication authentication,
            String resource,
            String permission
    );
}
