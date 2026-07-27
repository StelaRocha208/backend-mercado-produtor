package br.com.mercadoprodutor.historico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record VisitHistoryResponseDTO(
        String id,
        String produtorNome,
        String produtorCpf,
        LocalDate dataVisita,
        String secao,
        String numeroEspaco,
        LocalTime horarioEntrada,
        LocalTime horarioSaida,
        String tempoPermanencia,
        BigDecimal valorCobrado,
        String statusPagamento
) {
}
