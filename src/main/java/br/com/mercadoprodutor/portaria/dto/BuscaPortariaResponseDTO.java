package br.com.mercadoprodutor.portaria.dto;

import java.util.List;

public record BuscaPortariaResponseDTO(

        String usuarioId,
        String nome,
        String cpf,
        String telefone,
        boolean ativo,
        List<String> perfis,
        List<VeiculoPortariaDTO> veiculos

) {
}