package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidatasistemas.starterPackage.controller.dto.request.LoginRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.response.LoginResponse;
import br.com.digidatasistemas.starterPackage.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Autenticação de usuários e emissão de JWT")
public class AuthController {

    private final IAuthService service;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica com CPF e senha e retorna um token JWT e as permissões do usuário.")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                service.login(request)
        );
    }
}
