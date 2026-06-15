package br.com.mercadoprodutor.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank(message = "O login (e-mail ou CPF) não pode ser vazio")
        String login,

        @NotBlank(message = "Senha não pode ser vazia")
        String senha
) {}
