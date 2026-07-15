package br.com.mercadoprodutor.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TrocarPerfilDTO(
        @NotBlank(message = "O novo perfil é obrigatório!")
        String novoPerfil
) {}