package br.com.digidatasistemas.starterPackage.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {

    String generateToken(UserDetails usuario);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails usuario);
    long getExpirationMillis();
}
