package br.com.mercadoprodutor.produtores.dto;

import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;

import java.util.List;
import java.util.Set;

public record ProdutorDetalhesDTO(
        String tipo,
        String id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String statusAcesso,
        Set<PerfilUsuario> perfis,
        String dataCriacao,
        List<VeiculoResponseDTO> veiculos
) {}