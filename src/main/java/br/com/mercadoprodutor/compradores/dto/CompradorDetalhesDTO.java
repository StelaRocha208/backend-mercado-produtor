package br.com.mercadoprodutor.compradores.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

public record CompradorDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String statusAcesso,
        PerfilUsuario perfil,
        String dataCriacao
) {}