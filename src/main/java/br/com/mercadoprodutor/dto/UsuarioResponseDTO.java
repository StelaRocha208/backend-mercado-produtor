package br.com.mercadoprodutor.dto;

import br.com.mercadoprodutor.models.PerfilUsuario;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        String statusAcesso,
        PerfilUsuario perfil
) {
}