package br.com.digidatasistemas.starterPackage.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 números")
        String cpf,
        @NotBlank(message = "Senha é obrigatória")
        String password
) {}
