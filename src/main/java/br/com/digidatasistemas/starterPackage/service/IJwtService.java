package br.com.digidatasistemas.starterPackage.service;

import br.com.digidatasistemas.starterPackage.model.Usuario;

public interface IJwtService {

    String generateToken(Usuario usuario);
    String extractUsername(String token);
}
