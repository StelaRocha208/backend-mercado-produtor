package br.com.mercadoprodutor.portaria.dto;

import java.util.List;

public record BuscaPortariaResponseDTO(

        String usuarioId,
        String nome,
        String cpf,
        String telefone,
        Boolean ativo,

        Boolean inadimplente,
        String justificativaInadimplencia,

        List<String> perfis,
        List<VeiculoPortariaDTO> veiculos
) {
}