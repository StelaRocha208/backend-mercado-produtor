package br.com.mercadoprodutor.produtores.dto;

import jakarta.validation.constraints.NotBlank;

public record VeiculoDTO(
        @NotBlank(message = "A placa do veículo é obrigatória")
        String placa,

        @NotBlank(message = "O tipo do veículo é obrigatório")
        String tipo,

        String marca,
        String modelo,
        String cor
) {}
