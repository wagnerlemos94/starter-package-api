package br.com.digidatasistemas.starterPackage.service;

import br.com.digidatasistemas.starterPackage.model.User;

public interface IJwtService {

    String generateToken(User user);
    String extractUsername(String token);
}
