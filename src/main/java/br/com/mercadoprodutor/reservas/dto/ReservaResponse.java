package br.com.mercadoprodutor.reservas.dto;

import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Apresenta os dados comuns a todos os tipos de reserva.
 */
public record ReservaResponse(
        String id,

        String produtorId,
        String usuarioId,
        String produtorNome,

        String espacoId,
        String espacoNumero,
        String secaoNome,
        TipoSecao secaoTipo,

        LocalDate dataInicio,
        LocalDate dataFim,
        LocalDate dataEncerramento,

        TipoReserva tipoReserva,
        StatusReserva statusReserva,
        String reservaOrigemId,

        String observacao,
        String motivoEncerramento,

        BigDecimal areaM2,
        BigDecimal tarifaPorM2,
        Integer diasUso,
        BigDecimal valorTaxaSolo,

        LocalDateTime dataCriacao
) {
}
