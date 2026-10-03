package br.com.digidatasistemas.starterPackage.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.*;

public record UsuarioContaRequest(
        @NotBlank(message = MSG_NOME_OBRIGATORIO)
        @Size(max = 150, message = MSG_NOME_MAXIMO_150)
        String nome,
        @Size(min = 8, max = 72, message = MSG_SENHA_TAMANHO_INVALIDO)
        @Pattern(regexp = "(?s).*\\S.*", message = MSG_SENHA_OBRIGATORIA)
        String senha,
        @Size(max = 72, message = MSG_SENHA_TAMANHO_INVALIDO)
        String senhaAtual
) {
}
