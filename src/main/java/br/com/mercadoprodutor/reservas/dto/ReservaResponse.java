package br.com.mercadoprodutor.reservas.dto;

import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.reservas.model.StatusReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservaResponse(
        String id,

        String produtorId,
        String produtorNome,

        String espacoId,
        String espacoNumero,
        String secaoNome,
        TipoSecao secaoTipo,

        LocalDate dataInicio,
        LocalDate dataFim,

        StatusReserva statusReserva,

        String observacao,

        LocalDateTime dataCriacao
) {
}