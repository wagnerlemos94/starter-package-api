package br.com.digidatasistemas.starterPackage.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

public record LoginRequest(
        @NotBlank(message = MSG_CPF_OBRIGATORIO)
        @Pattern(regexp = "\\d{11}", message = MSG_CPF_INVALIDO)
        String cpf,
        @NotBlank(message = MSG_SENHA_OBRIGATORIA)
        String password
) {}
