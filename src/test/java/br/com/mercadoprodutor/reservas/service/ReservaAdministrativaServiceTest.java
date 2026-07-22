package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.reservas.dto.CriarReservaAdministrativaRequest;
import br.com.mercadoprodutor.reservas.dto.CriarSubstituicaoRequest;
import br.com.mercadoprodutor.reservas.dto.EncerrarReservaRequest;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ProdutorReservaRepository;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaAdministrativaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ProdutorReservaRepository produtorReservaRepository;
    @Mock
    private ReservaMapper reservaMapper;
    @Mock
    private ValidadorDisponibilidadeReserva validadorDisponibilidade;
    @Mock
    private Produtor produtor;
    @Mock
    private Produtor substituto;
    @Mock
    private Espaco espaco;
    @Mock
    private Secao secao;

    private ReservaAdministrativaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaAdministrativaService(
                reservaRepository,
                produtorReservaRepository,
                reservaMapper,
                new PoliticaPeriodoReserva(),
                validadorDisponibilidade
        );
    }

    @Test
    void criaReservaTitularDeBoxPorCincoAnosSemDataFinal() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        configurarSecao(TipoSecao.BOX);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.criar(new CriarReservaAdministrativaRequest(
                "usuario-1",
                "espaco-1",
                TipoReserva.TITULAR,
                inicio,
                null,
                null
        ));

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());

        Reserva salva = captor.getValue();
        assertEquals(TipoReserva.TITULAR, salva.getTipoReserva());
        assertEquals(inicio.plusYears(5).minusDays(1), salva.getDataFim());
    }

    @Test
    void rejeitaReservaAdministrativaVolatilAcimaDeUmMes() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        configurarSecao(TipoSecao.VOLATIL);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);

        assertThrows(ResponseStatusException.class, () ->
                service.criar(new CriarReservaAdministrativaRequest(
                        "usuario-1",
                        "espaco-1",
                        TipoReserva.PRIORITARIA,
                        inicio,
                        inicio.plusMonths(1),
                        null
                ))
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void administradorCriaReservaDiariaConfirmadaEmEspacoVolatil() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        configurarSecao(TipoSecao.VOLATIL);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.criar(new CriarReservaAdministrativaRequest(
                "usuario-1",
                "espaco-1",
                TipoReserva.DIARIA,
                inicio,
                null,
                "Reserva presencial"
        ));

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());

        Reserva salva = captor.getValue();
        assertEquals(TipoReserva.DIARIA, salva.getTipoReserva());
        assertEquals(StatusReserva.CONFIRMADA, salva.getStatusReserva());
        assertEquals(inicio, salva.getDataFim());
    }

    @Test
    void administradorCriaReservaPrioritariaVolatilPorAteUmMes() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        configurarSecao(TipoSecao.VOLATIL);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.criar(new CriarReservaAdministrativaRequest(
                "usuario-1",
                "espaco-1",
                TipoReserva.PRIORITARIA,
                inicio,
                null,
                null
        ));

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());

        Reserva salva = captor.getValue();
        assertEquals(TipoReserva.PRIORITARIA, salva.getTipoReserva());
        assertEquals(StatusReserva.CONFIRMADA, salva.getStatusReserva());
        assertEquals(inicio.plusMonths(1).minusDays(1), salva.getDataFim());
    }

    @Test
    void rejeitaReservaDiariaAdministrativaComMaisDeUmDia() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        configurarSecao(TipoSecao.VOLATIL);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);

        assertThrows(ResponseStatusException.class, () ->
                service.criar(new CriarReservaAdministrativaRequest(
                        "usuario-1",
                        "espaco-1",
                        TipoReserva.DIARIA,
                        inicio,
                        inicio.plusDays(1),
                        null
                ))
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void substituicaoProvisoriaPreservaDataFinalDaReservaOriginal() {
        LocalDate hoje = LocalDate.now();
        LocalDate fimOriginal = hoje.plusYears(3);
        Reserva original = Reserva.criarTitular(
                produtor,
                espaco,
                hoje.minusYears(2),
                fimOriginal,
                null
        );
        original.encerrar(hoje, "Saída do produtor");

        when(produtor.getId()).thenReturn("produtor-original");
        when(substituto.getId()).thenReturn("produtor-substituto");
        when(espaco.getId()).thenReturn("espaco-1");
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(original));
        when(produtorReservaRepository.findByUsuarioId("usuario-2"))
                .thenReturn(Optional.of(substituto));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.criarSubstituicao(
                "reserva-1",
                new CriarSubstituicaoRequest("usuario-2", null, null)
        );

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());

        Reserva salva = captor.getValue();
        assertEquals(TipoReserva.PROVISORIA, salva.getTipoReserva());
        assertEquals(hoje.plusDays(1), salva.getDataInicio());
        assertEquals(fimOriginal, salva.getDataFim());
        assertSame(original, salva.getReservaOrigem());
    }

    @Test
    void exigeEncerramentoAntesDeCriarSubstituicao() {
        LocalDate hoje = LocalDate.now();
        Reserva reservaConfirmada = Reserva.criarTitular(
                produtor,
                espaco,
                hoje.minusDays(1),
                hoje.plusYears(1),
                null
        );
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaConfirmada));

        assertThrows(ResponseStatusException.class, () ->
                service.criarSubstituicao(
                        "reserva-1",
                        new CriarSubstituicaoRequest(
                                "usuario-2",
                                hoje.plusDays(1),
                                null
                        )
                )
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoEncerraReservaAdministrativaCancelada() {
        LocalDate hoje = LocalDate.now();
        Reserva reservaCancelada = Reserva.criarTitular(
                produtor,
                espaco,
                hoje.minusDays(1),
                hoje.plusYears(1),
                null
        );
        reservaCancelada.setStatusReserva(StatusReserva.CANCELADA);
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaCancelada));

        assertThrows(ResponseStatusException.class, () ->
                service.encerrar(
                        "reserva-1",
                        new EncerrarReservaRequest(hoje, "Cancelada")
                )
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void encerraReservaDiariaAguardandoConfirmacao() {
        LocalDate hoje = LocalDate.now();
        Reserva reservaDiaria = Reserva.criarDiaria(
                produtor,
                espaco,
                hoje,
                hoje,
                null
        );
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaDiaria));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.encerrar(
                "reserva-1",
                new EncerrarReservaRequest(hoje, "Encerrada pelo administrador")
        );

        assertEquals(StatusReserva.ENCERRADA, reservaDiaria.getStatusReserva());
        assertEquals(hoje, reservaDiaria.getDataEncerramento());
    }

    @Test
    void cancelaReservaFuturaAoEncerrar() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.plusDays(1);
        Reserva reservaFutura = Reserva.criarDiaria(
                produtor,
                espaco,
                inicio,
                inicio,
                null
        );
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaFutura));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.encerrar(
                "reserva-1",
                new EncerrarReservaRequest(hoje, "Cancelada pelo administrador")
        );

        assertEquals(StatusReserva.CANCELADA, reservaFutura.getStatusReserva());
        assertNull(reservaFutura.getDataEncerramento());
        assertEquals(
                "Cancelada pelo administrador",
                reservaFutura.getMotivoEncerramento()
        );
    }

    @Test
    void reservaVolatilEncerradaDeixaDeBloquearEspaco() {
        LocalDate hoje = LocalDate.now();
        Reserva reservaPrioritaria = Reserva.criarPrioritaria(
                produtor,
                espaco,
                hoje,
                hoje.plusDays(10),
                null
        );
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaPrioritaria));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.encerrar(
                "reserva-1",
                new EncerrarReservaRequest(hoje, "Liberação do espaço")
        );

        assertEquals(
                StatusReserva.ENCERRADA,
                reservaPrioritaria.getStatusReserva()
        );
        assertFalse(
                StatusReserva.bloqueantes().contains(StatusReserva.ENCERRADA)
        );
    }

    @Test
    void naoPermiteSubstituicaoEmReservaVolatil() {
        LocalDate hoje = LocalDate.now();
        Reserva reservaPrioritaria = Reserva.criarPrioritaria(
                produtor,
                espaco,
                hoje.minusDays(1),
                hoje.plusDays(10),
                null
        );
        reservaPrioritaria.encerrar(hoje, "Saída do produtor");
        when(reservaRepository.findByIdParaAtualizacao("reserva-1"))
                .thenReturn(Optional.of(reservaPrioritaria));

        assertThrows(ResponseStatusException.class, () ->
                service.criarSubstituicao(
                        "reserva-1",
                        new CriarSubstituicaoRequest(
                                "usuario-2",
                                hoje.plusDays(1),
                                null
                        )
                )
        );

        verify(produtorReservaRepository, never()).findByUsuarioId(any());
        verify(reservaRepository, never()).save(any());
    }

    private void configurarSecao(TipoSecao tipoSecao) {
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(tipoSecao);
    }
}
