package br.com.mercadoprodutor.espacos.service;

import br.com.mercadoprodutor.espacos.dto.AtualizarAreaSecaoRequest;
import br.com.mercadoprodutor.espacos.dto.AtualizarTarifaSecaoRequest;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.dto.SecaoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.espacos.repository.SecaoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EspacoServiceTest {

    @Mock
    private EspacoRepository espacoRepository;
    @Mock
    private SecaoRepository secaoRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private Espaco espaco;
    @Mock
    private Secao secao;

    @InjectMocks
    private EspacoService service;

    @Test
    void listaSecoesComTarifaPorMetroQuadrado() {
        Secao secaoAtiva = new Secao(
                "Box",
                TipoSecao.BOX,
                40,
                1
        );
        secaoAtiva.setTaxaPorM2(new BigDecimal("2.50"));
        when(secaoRepository.findByAtivaTrueOrderByOrdemVisualAsc())
                .thenReturn(List.of(secaoAtiva));

        List<SecaoResponse> resposta = service.listarSecoes();

        assertEquals(1, resposta.size());
        assertEquals(new BigDecimal("2.50"), resposta.getFirst().taxaPorM2());
    }

    @Test
    void atualizaTarifaPorMetroQuadradoDaSecao() {
        Secao secaoExistente = new Secao(
                "Pedra",
                TipoSecao.PEDRA,
                40,
                1
        );
        when(secaoRepository.findById("secao-1"))
                .thenReturn(Optional.of(secaoExistente));

        SecaoResponse resposta = service.atualizarTarifa(
                "secao-1",
                new AtualizarTarifaSecaoRequest(new BigDecimal("1.80"))
        );

        assertEquals(new BigDecimal("1.80"), secaoExistente.getTaxaPorM2());
        assertEquals(new BigDecimal("1.80"), resposta.taxaPorM2());
    }

    @Test
    void atualizaAreaDeTodasAsVagasAtivasDaSecao() {
        when(secaoRepository.existsById("secao-1")).thenReturn(true);

        service.atualizarArea(
                "secao-1",
                new AtualizarAreaSecaoRequest(new BigDecimal("40.00"))
        );

        verify(espacoRepository).atualizarAreaM2PorSecao(
                "secao-1",
                new BigDecimal("40.00")
        );
    }

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
        when(secao.getTaxaPorM2()).thenReturn(new BigDecimal("2.50"));
        when(espaco.getAreaM2()).thenReturn(new BigDecimal("12.00"));
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
        when(secao.getTaxaPorM2()).thenReturn(new BigDecimal("2.50"));
        when(espaco.getAreaM2()).thenReturn(new BigDecimal("12.00"));
        when(espaco.getAtivo()).thenReturn(true);
        when(espaco.getStatusOcupacao()).thenReturn(StatusOcupacao.LIVRE);

        EspacoResponse resposta = service
                .listarEspacos(null, null, null)
                .getFirst();

        assertFalse(resposta.selecionavel());
        assertTrue(resposta.selecionavelAdministracao());
        assertEquals(new BigDecimal("2.50"), resposta.tarifaPorM2());
        assertEquals(new BigDecimal("30.00"), resposta.valorDiaria());
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
