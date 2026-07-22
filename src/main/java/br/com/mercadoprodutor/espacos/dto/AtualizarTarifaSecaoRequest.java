package br.com.mercadoprodutor.espacos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarTarifaSecaoRequest(
        @NotNull(message = "A tarifa por metro quadrado é obrigatória.")
        @DecimalMin(
                value = "0.00",
                message = "A tarifa por metro quadrado não pode ser negativa."
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "A tarifa deve ter no máximo duas casas decimais."
        )
        BigDecimal taxaPorM2
) {
}
