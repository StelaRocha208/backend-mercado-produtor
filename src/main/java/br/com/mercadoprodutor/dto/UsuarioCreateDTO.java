package br.com.mercadoprodutor.dto;

import br.com.mercadoprodutor.models.PerfilUsuario;

public record UsuarioCreateDTO(
        String nome,
        String email,
        String senha,
        PerfilUsuario perfil,
        String cpf,
        String telefone
) {
}