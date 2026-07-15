package br.com.mercadoprodutor.produtores.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

import java.util.List;

public record ProdutorDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String statusAcesso,
        PerfilUsuario perfil,
        String dataCriacao,
        List<VeiculoResponseDTO> veiculos
) {}