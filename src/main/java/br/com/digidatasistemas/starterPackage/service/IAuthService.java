package br.com.digidatasistemas.starterPackage.service;

import br.com.digidatasistemas.starterPackage.controller.dto.request.LoginRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;

public interface IAuthService {
    LoginResponse login(LoginRequest request);
}
