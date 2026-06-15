package br.com.mercadoprodutor.usuarios.dto;

import br.com.mercadoprodutor.usuarios.model.Usuario;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        String statusAcesso,
        PerfilUsuario perfis
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getStatusAcesso(),
                usuario.getPerfis()
        );
    }
}
