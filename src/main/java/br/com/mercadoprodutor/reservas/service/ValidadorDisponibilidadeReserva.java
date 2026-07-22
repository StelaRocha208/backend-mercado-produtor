package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/** Centraliza a validação de disponibilidade usada na criação de reservas. */
@Component
@RequiredArgsConstructor
public class ValidadorDisponibilidadeReserva {

    private final EspacoRepository espacoRepository;
    private final ReservaRepository reservaRepository;

    public Espaco buscarEspacoReservavel(String espacoId) {
        Espaco espaco = espacoRepository.findByIdParaAtualizacao(espacoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Espaço não encontrado."
                ));

        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            throw conflito("O espaço está inativo.");
        }

        if (espaco.getStatusOcupacao() != StatusOcupacao.LIVRE) {
            throw conflito("O espaço não está disponível para reserva.");
        }

        return espaco;
    }

    public void validarAusenciaDeConflito(
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        boolean existeConflito = reservaRepository.existsConflitoDePeriodo(
                espaco.getId(),
                dataInicio,
                dataFim,
                StatusReserva.bloqueantes()
        );

        if (existeConflito) {
            throw conflito(
                    "O espaço já possui reserva para o período informado."
            );
        }
    }

    private ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }
}
