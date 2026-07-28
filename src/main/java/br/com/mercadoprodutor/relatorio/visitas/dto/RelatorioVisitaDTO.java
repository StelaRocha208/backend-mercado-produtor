package br.com.mercadoprodutor.relatorio.visitas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RelatorioVisitaDTO(

        String produtor,

        String placa,
        String tipoVeiculo,

        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,

        String secao,
        String espaco,

        BigDecimal taxaVeiculo,
        BigDecimal taxaSolo,
        BigDecimal valorTotal

) {
}