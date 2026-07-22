package br.com.mercadoprodutor.espacos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarAreaSecaoRequest(
        @NotNull(message = "A área da vaga é obrigatória.")
        @DecimalMin(
                value = "0.01",
                message = "A área da vaga deve ser maior que zero."
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "A área deve ter no máximo duas casas decimais."
        )
        BigDecimal areaM2
) {
}
