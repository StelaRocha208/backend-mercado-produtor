package br.com.mercadoprodutor.reservas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Dados para encerrar ou cancelar antecipadamente uma reserva. */
public record EncerrarReservaRequest(
        @NotNull(message = "A data de encerramento é obrigatória.")
        @PastOrPresent(message = "A data de encerramento não pode estar no futuro.")
        LocalDate dataEncerramento,

        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres.")
        String motivo
) {
}
