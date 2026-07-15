package br.com.mercadoprodutor.produtores.dto;

public record VeiculoResponseDTO(
        String placa,
        String tipo,
        String marca,
        String modelo,
        String cor
) {}