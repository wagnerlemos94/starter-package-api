package br.com.digidatasistemas.starterPackage.integration;

import br.com.digidata.crud.exception.ResourceNotFoundException;
import br.com.digidatasistemas.starterPackage.exception.ConflictException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SecurityTestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "ok";
    }

    @GetMapping("/permission")
    @PreAuthorize("hasAuthority('USUARIO:VIEW')")
    public String permissionEndpoint() {
        return "ok";
    }

    @GetMapping("/conflict")
    public String conflict() {
        throw new ConflictException("Recurso duplicado");
    }

    @GetMapping("/not-found")
    public String notFound() {
        throw new ResourceNotFoundException("Recurso não encontrado");
    }
}
