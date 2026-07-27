package br.com.mercadoprodutor.historico.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record VisitHistoryFilterDTO(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicio,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFim,

        String secao,

        String statusPagamento,

        String nomeProdutor,

        String cpfProdutor
) {
}
