package br.com.mercadoprodutor.historico.dto;

import java.util.List;

public record VisitHistoryPageResponseDTO(
        List<VisitHistoryResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        VisitHistorySummaryDTO summary
) {
}
