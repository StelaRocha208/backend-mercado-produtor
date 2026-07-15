package br.com.mercadoprodutor.compradores.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

import java.util.Set;

public record CompradorDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String statusAcesso,
        Set<PerfilUsuario> perfis,
        String dataCriacao
) {}