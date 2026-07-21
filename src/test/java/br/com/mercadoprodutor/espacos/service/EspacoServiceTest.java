package br.com.mercadoprodutor.espacos.service;

import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EspacoServiceTest {

    @Mock
    private EspacoRepository espacoRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private Espaco espaco;
    @Mock
    private Secao secao;

    @InjectMocks
    private EspacoService service;

    @Test
    void mapaMarcaReservaAdministrativaComoEspacoOcupado() {
        LocalDate hoje = LocalDate.now();
        when(espacoRepository.findAllAtivosOrdenados())
                .thenReturn(List.of(espaco));
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.reservasVolateis()
        )).thenReturn(Set.of());
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.ocupacoesFixas()
        )).thenReturn(Set.of("espaco-1"));
        when(espaco.getId()).thenReturn("espaco-1");
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(TipoSecao.BOX);
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(StatusOcupacao.LIVRE);

        List<EspacoResponse> resposta = service.listarEspacos(null, null, null);

        assertEquals(StatusOcupacao.OCUPADO, resposta.getFirst().statusOcupacao());
    }

    @Test
    void mapaPreservaIndisponibilidadeDefinidaNoEspaco() {
        LocalDate hoje = LocalDate.now();
        when(espacoRepository.findAllAtivosOrdenados())
                .thenReturn(List.of(espaco));
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.reservasVolateis()
        )).thenReturn(Set.of());
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.ocupacoesFixas()
        )).thenReturn(Set.of("espaco-1"));
        when(espaco.getId()).thenReturn("espaco-1");
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(TipoSecao.VOLATIL);
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(
                StatusOcupacao.INDISPONIVEL
        );

        List<EspacoResponse> resposta = service.listarEspacos(null, null, null);

        assertEquals(
                StatusOcupacao.INDISPONIVEL,
                resposta.getFirst().statusOcupacao()
        );
    }

    @Test
    void mapaPermiteAdministracaoSelecionarBoxLivre() {
        LocalDate hoje = LocalDate.now();
        when(espacoRepository.findAllAtivosOrdenados())
                .thenReturn(List.of(espaco));
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.reservasVolateis()
        )).thenReturn(Set.of());
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.ocupacoesFixas()
        )).thenReturn(Set.of());
        when(espaco.getId()).thenReturn("espaco-1");
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(TipoSecao.BOX);
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(StatusOcupacao.LIVRE);

        EspacoResponse resposta = service
                .listarEspacos(null, null, null)
                .getFirst();

        assertFalse(resposta.selecionavel());
        assertTrue(resposta.selecionavelAdministracao());
        assertNull(resposta.motivoBloqueioAdministracao());
    }

    @Test
    void mapaMarcaReservaPrioritariaComoReservada() {
        LocalDate hoje = LocalDate.now();
        when(espacoRepository.findAllAtivosOrdenados())
                .thenReturn(List.of(espaco));
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.reservasVolateis()
        )).thenReturn(Set.of("espaco-1"));
        when(reservaRepository.findEspacosReservadosNoPeriodo(
                hoje,
                hoje,
                StatusReserva.bloqueantes(),
                TipoReserva.ocupacoesFixas()
        )).thenReturn(Set.of());
        when(espaco.getId()).thenReturn("espaco-1");
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(TipoSecao.VOLATIL);
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(StatusOcupacao.LIVRE);

        EspacoResponse resposta = service
                .listarEspacos(null, null, null)
                .getFirst();

        assertEquals(StatusOcupacao.RESERVADO, resposta.statusOcupacao());
    }
}
