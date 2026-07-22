package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorDisponibilidadeReservaTest {

    @Mock
    private EspacoRepository espacoRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private Espaco espaco;

    private ValidadorDisponibilidadeReserva validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorDisponibilidadeReserva(
                espacoRepository,
                reservaRepository
        );
    }

    @Test
    void retornaEspacoAtivoELivreComBloqueio() {
        when(espacoRepository.findByIdParaAtualizacao("espaco-1"))
                .thenReturn(Optional.of(espaco));
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(StatusOcupacao.LIVRE);

        Espaco resultado = validador.buscarEspacoReservavel("espaco-1");

        assertSame(espaco, resultado);
    }

    @Test
    void rejeitaEspacoIndisponivel() {
        when(espacoRepository.findByIdParaAtualizacao("espaco-1"))
                .thenReturn(Optional.of(espaco));
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao())
                .thenReturn(StatusOcupacao.INDISPONIVEL);

        assertThrows(
                ResponseStatusException.class,
                () -> validador.buscarEspacoReservavel("espaco-1")
        );
    }

    @Test
    void rejeitaConflitoDePeriodo() {
        LocalDate data = LocalDate.now().plusDays(1);
        when(espaco.getId()).thenReturn("espaco-1");
        when(reservaRepository.existsConflitoDePeriodo(
                "espaco-1",
                data,
                data,
                StatusReserva.bloqueantes()
        )).thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> validador.validarAusenciaDeConflito(
                        espaco,
                        data,
                        data
                )
        );
    }
}
