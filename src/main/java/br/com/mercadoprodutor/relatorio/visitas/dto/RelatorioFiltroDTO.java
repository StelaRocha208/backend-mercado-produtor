package br.com.mercadoprodutor.relatorio.visitas.dto;

import java.time.LocalDate;

public record RelatorioFiltroDTO(

        LocalDate dataInicio,
        LocalDate dataFim,
        String secao,
        String produtor

) {
}
