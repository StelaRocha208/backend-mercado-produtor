package br.com.mercadoprodutor.reservas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarReservaRequest(
        @NotBlank(message = "O espaço é obrigatório.")
        String espacoId,

        @NotNull(message = "A data inicial é obrigatória.")
        @FutureOrPresent(message = "A data inicial não pode estar no passado.")
        LocalDate dataInicio,

        @NotNull(message = "A data final é obrigatória.")
        @FutureOrPresent(message = "A data final não pode estar no passado.")
        LocalDate dataFim,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacao
) {
}
