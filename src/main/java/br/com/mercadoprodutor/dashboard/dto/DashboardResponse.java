package br.com.mercadoprodutor.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
        LocalDate dataReferencia,
        LocalDateTime atualizadoEm,
        IndicadorResponse acessosDoDia,
        IndicadorResponse receitaGerada,
        IndicadorResponse veiculosPresentes,
        IndicadorResponse produtoresInadimplentes,
        IndicadorResponse ocupacaoGeral,
        List<OcupacaoSecaoResponse> ocupacaoPorSecao,
        List<AcessosPorHoraResponse> acessosPorHora,
        List<AcessosPorHoraResponse> acessosPorHoraOntem
) {
}
