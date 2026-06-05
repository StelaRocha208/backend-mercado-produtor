package br.com.mercadoprodutor.dto;

import br.com.mercadoprodutor.models.PerfilUsuario;

public record RegisterDTO(String email, String senha, PerfilUsuario perfil) {
}
