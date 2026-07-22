package br.com.mercadoprodutor.reservas.dto;

import br.com.mercadoprodutor.reservas.model.TipoReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Dados usados pelo administrador para reservar um espaço a um produtor.
 */
public record CriarReservaAdministrativaRequest(
        @NotBlank(message = "O usuário produtor é obrigatório.")
        String usuarioId,

        @NotBlank(message = "O espaço é obrigatório.")
        String espacoId,

        @NotNull(message = "O tipo da reserva é obrigatório.")
        TipoReserva tipoReserva,

        @NotNull(message = "A data inicial é obrigatória.")
        @FutureOrPresent(message = "A data inicial não pode estar no passado.")
        LocalDate dataInicio,

        @FutureOrPresent(message = "A data final não pode estar no passado.")
        LocalDate dataFim,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacao
) {
}
