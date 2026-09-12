package br.com.digidatasistemas.starterPackage.service;

import org.springframework.security.core.Authentication;

public interface IAutorizacaoService {

    boolean hasPermission(
            Authentication authentication,
            String resource,
            String permission
    );
}
