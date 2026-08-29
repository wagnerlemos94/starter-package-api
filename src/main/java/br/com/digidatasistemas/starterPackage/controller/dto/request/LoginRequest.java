package br.com.digidatasistemas.starterPackage.controller.dto.request;

public record LoginRequest(
        String cpf,
        String password
) {}