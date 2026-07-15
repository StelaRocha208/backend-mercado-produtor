package br.com.mercadoprodutor.usuarios.dto;

import br.com.mercadoprodutor.usuarios.model.Usuario;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

import java.util.Set;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        String statusAcesso,
        Set<PerfilUsuario> perfis
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
