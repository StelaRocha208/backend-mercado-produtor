package br.com.mercadoprodutor.usuarios.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

import java.util.Set;

public record UsuarioDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String statusAcesso,
        Set<PerfilUsuario> perfis,
        String dataCriacao
) {}