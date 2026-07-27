package br.com.mercadoprodutor.historico.dto;

import java.math.BigDecimal;

public record VisitHistorySummaryDTO(
        long totalVisitas,
        BigDecimal valorTotalPago,
        BigDecimal valorTotalPendente,
        String tempoMedioPermanencia
) {
}
