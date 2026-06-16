package br.com.mercadoprodutor.portaria.dto;

public record RegistroEntradaDTO(
        String produtorId,
        String veiculoId,
        String secao,
        String espaco,
        String numeroNF,
        String observacao
) {
}