package br.com.mercadoprodutor.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O status de acesso é obrigatório (ATIVO/INATIVO)")
        String statusAcesso,

        String senha
) {}
