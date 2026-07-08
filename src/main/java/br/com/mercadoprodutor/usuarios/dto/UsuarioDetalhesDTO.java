package br.com.mercadoprodutor.usuarios.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

public record UsuarioDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String statusAcesso,
        PerfilUsuario perfil,
        String dataCriacao
) {}