package br.com.mercadoprodutor.reservas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Dados para substituir provisoriamente um produtor que deixou o espaço.
 */
public record CriarSubstituicaoRequest(
        @NotBlank(message = "O usuário produtor substituto é obrigatório.")
        String usuarioId,

        @FutureOrPresent(message = "A data inicial não pode estar no passado.")
        LocalDate dataInicio,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
        String observacao
) {
}
