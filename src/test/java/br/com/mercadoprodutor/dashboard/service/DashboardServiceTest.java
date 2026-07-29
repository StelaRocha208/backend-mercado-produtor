package br.com.mercadoprodutor.dashboard.service;

import br.com.mercadoprodutor.dashboard.dto.DashboardResponse;
import br.com.mercadoprodutor.dashboard.repository.AcessoHoraProjection;
import br.com.mercadoprodutor.dashboard.repository.DashboardQueryRepository;
import br.com.mercadoprodutor.dashboard.repository.EspacoOcupacaoProjection;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final ZoneId FUSO_HORARIO = ZoneId.of("America/Bahia");

    @Mock
    private DashboardQueryRepository repository;

    private DashboardService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-07-25T13:30:00Z"),
                FUSO_HORARIO
        );
        service = new DashboardService(repository, clock);
    }

    @Test
    void deveMontarTodosOsIndicadoresDoDashboard() {
        when(repository.contarAcessosEntre(any(), any()))
                .thenReturn(42L, 35L);
        when(repository.somarReceitaVeiculosEntre(any(), any()))
                .thenReturn(
                        new BigDecimal("100.00"),
                        new BigDecimal("80.00")
                );
        when(repository.somarReceitaEspacosEm(any()))
                .thenReturn(
                        new BigDecimal("200.00"),
                        new BigDecimal("160.00")
                );
        when(repository.contarVeiculosPresentesEm(any()))
                .thenReturn(12L, 10L);
        when(repository.contarProdutoresInadimplentes()).thenReturn(4L);
        when(repository.buscarEspacosAtivos()).thenReturn(List.of(
                new EspacoOcupacaoProjection(
                        "pedra-1",
                        TipoSecao.PEDRA,
                        "Pedra",
                        1,
                        StatusOcupacao.LIVRE
                ),
                new EspacoOcupacaoProjection(
                        "pedra-2",
                        TipoSecao.PEDRA,
                        "Pedra",
                        1,
                        StatusOcupacao.LIVRE
                ),
                new EspacoOcupacaoProjection(
                        "box-1",
                        TipoSecao.BOX,
                        "Box",
                        2,
                        StatusOcupacao.LIVRE
                )
        ));
        when(repository.buscarEspacosReservadosEm(any()))
                .thenReturn(
                        Set.of("pedra-1", "box-1"),
                        Set.of("pedra-2")
                );
        when(repository.buscarAcessosPorHora(any(), any()))
                .thenReturn(
                        List.of(
                                new AcessoHoraProjection(8, 3),
                                new AcessoHoraProjection(10, 2)
                        ),
                        List.of(
                                new AcessoHoraProjection(8, 2),
                                new AcessoHoraProjection(9, 1)
                        )
                );

        DashboardResponse resposta = service.obterIndicadores();

        assertThat(resposta.dataReferencia().toString())
                .isEqualTo("2026-07-25");
        assertThat(resposta.acessosDoDia().valor())
                .isEqualByComparingTo("42");
        assertThat(resposta.acessosDoDia().variacaoPercentual())
                .isEqualByComparingTo("20.0");
        assertThat(resposta.receitaGerada().valor())
                .isEqualByComparingTo("300.00");
        assertThat(resposta.receitaGerada().variacaoPercentual())
                .isEqualByComparingTo("25.0");
        assertThat(resposta.veiculosPresentes().valor())
                .isEqualByComparingTo("12");
        assertThat(resposta.produtoresInadimplentes().valor())
                .isEqualByComparingTo("4");
        assertThat(resposta.produtoresInadimplentes().comparavel()).isFalse();
        assertThat(resposta.ocupacaoGeral().valor())
                .isEqualByComparingTo("66.7");
        assertThat(resposta.ocupacaoGeral().comparavel()).isTrue();

        assertThat(resposta.ocupacaoPorSecao()).hasSize(2);
        assertThat(resposta.ocupacaoPorSecao().getFirst().percentual())
                .isEqualByComparingTo("50.0");
        assertThat(resposta.ocupacaoPorSecao().get(1).percentual())
                .isEqualByComparingTo("100.0");
        assertThat(
                resposta.ocupacaoPorSecao().get(1).variacaoPercentual()
        ).isEqualByComparingTo("100.0");

        assertThat(resposta.acessosPorHora()).hasSize(24);
        assertThat(resposta.acessosPorHora().get(8).total()).isEqualTo(3);
        assertThat(resposta.acessosPorHora().get(9).total()).isZero();
        assertThat(resposta.acessosPorHora().get(10).total()).isEqualTo(2);
        assertThat(resposta.acessosPorHoraOntem()).hasSize(24);
        assertThat(resposta.acessosPorHoraOntem().get(8).total()).isEqualTo(2);
        assertThat(resposta.acessosPorHoraOntem().get(9).total()).isEqualTo(1);
    }

    @Test
    void deveRetornarVariacaoZeroQuandoOsDoisPeriodosEstaoZerados() {
        when(repository.contarAcessosEntre(any(), any()))
                .thenReturn(0L, 0L);
        when(repository.somarReceitaVeiculosEntre(any(), any()))
                .thenReturn(BigDecimal.ZERO, BigDecimal.ZERO);
        when(repository.somarReceitaEspacosEm(any()))
                .thenReturn(BigDecimal.ZERO, BigDecimal.ZERO);
        when(repository.contarVeiculosPresentesEm(any()))
                .thenReturn(0L, 0L);
        when(repository.contarProdutoresInadimplentes()).thenReturn(0L);
        when(repository.buscarEspacosAtivos()).thenReturn(List.of());
        when(repository.buscarEspacosReservadosEm(any()))
                .thenReturn(Set.of(), Set.of());
        when(repository.buscarAcessosPorHora(any(), any()))
                .thenReturn(List.of());

        DashboardResponse resposta = service.obterIndicadores();

        assertThat(resposta.acessosDoDia().variacaoPercentual())
                .isEqualByComparingTo("0.0");
        assertThat(resposta.receitaGerada().variacaoPercentual())
                .isEqualByComparingTo("0.0");
        assertThat(resposta.veiculosPresentes().variacaoPercentual())
                .isEqualByComparingTo("0.0");
        assertThat(resposta.ocupacaoGeral().valor())
                .isEqualByComparingTo("0.0");
        assertThat(resposta.acessosPorHora())
                .allMatch(item -> item.total() == 0);
        assertThat(resposta.acessosPorHoraOntem())
                .allMatch(item -> item.total() == 0);
    }
}
