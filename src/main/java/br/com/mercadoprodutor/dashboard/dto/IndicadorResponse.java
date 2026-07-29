package br.com.mercadoprodutor.dashboard.dto;

import java.math.BigDecimal;

public record IndicadorResponse(
        BigDecimal valor,
        BigDecimal variacaoPercentual,
        boolean comparavel
) {
}
