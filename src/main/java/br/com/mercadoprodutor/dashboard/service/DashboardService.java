package br.com.mercadoprodutor.dashboard.service;

import br.com.mercadoprodutor.dashboard.dto.AcessosPorHoraResponse;
import br.com.mercadoprodutor.dashboard.dto.DashboardResponse;
import br.com.mercadoprodutor.dashboard.dto.IndicadorResponse;
import br.com.mercadoprodutor.dashboard.dto.OcupacaoSecaoResponse;
import br.com.mercadoprodutor.dashboard.repository.AcessoHoraProjection;
import br.com.mercadoprodutor.dashboard.repository.DashboardQueryRepository;
import br.com.mercadoprodutor.dashboard.repository.EspacoOcupacaoProjection;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {

    private static final int ESCALA_PERCENTUAL = 1;

    private final DashboardQueryRepository repository;
    private final Clock clock;

    public DashboardService(
            DashboardQueryRepository repository,
            @Qualifier("dashboardClock") Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obterIndicadores() {
        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDate hoje = agora.toLocalDate();
        LocalDate ontem = hoje.minusDays(1);

        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime inicioOntem = ontem.atStartOfDay();
        LocalDateTime mesmoHorarioOntem = agora.minusDays(1);

        long acessosHoje = repository.contarAcessosEntre(
                inicioHoje,
                agora
        );
        long acessosOntem = repository.contarAcessosEntre(
                inicioOntem,
                mesmoHorarioOntem
        );

        BigDecimal receitaHoje = repository.somarReceitaVeiculosEntre(
                inicioHoje,
                agora
        ).add(repository.somarReceitaEspacosEm(hoje));
        BigDecimal receitaOntem = repository.somarReceitaVeiculosEntre(
                inicioOntem,
                mesmoHorarioOntem
        ).add(repository.somarReceitaEspacosEm(ontem));

        long veiculosHoje = repository.contarVeiculosPresentesEm(agora);
        long veiculosOntem = repository.contarVeiculosPresentesEm(
                mesmoHorarioOntem
        );
        long produtoresInadimplentes = repository
                .contarProdutoresInadimplentes();

        List<EspacoOcupacaoProjection> espacos = repository
                .buscarEspacosAtivos();
        OcupacaoDashboard ocupacao = montarOcupacao(
                espacos,
                repository.buscarEspacosReservadosEm(hoje),
                repository.buscarEspacosReservadosEm(ontem)
        );

        List<AcessosPorHoraResponse> acessosPorHora = preencherTodasAsHoras(
                repository.buscarAcessosPorHora(inicioHoje, agora)
        );
        List<AcessosPorHoraResponse> acessosPorHoraOntem = preencherTodasAsHoras(
                repository.buscarAcessosPorHora(
                        inicioOntem,
                        mesmoHorarioOntem
                )
        );

        return new DashboardResponse(
                hoje,
                agora,
                indicadorComparavel(acessosHoje, acessosOntem),
                indicadorComparavel(receitaHoje, receitaOntem),
                indicadorComparavel(veiculosHoje, veiculosOntem),
                indicadorAtual(produtoresInadimplentes),
                ocupacao.indicadorGeral(),
                ocupacao.secoes(),
                acessosPorHora,
                acessosPorHoraOntem
        );
    }

    private OcupacaoDashboard montarOcupacao(
            List<EspacoOcupacaoProjection> espacos,
            Set<String> ocupadosHoje,
            Set<String> ocupadosOntem
    ) {
        Map<TipoSecao, OcupacaoAcumulada> porSecao = new LinkedHashMap<>();

        for (EspacoOcupacaoProjection espaco : espacos) {
            OcupacaoAcumulada acumulada = porSecao.computeIfAbsent(
                    espaco.secao(),
                    secao -> new OcupacaoAcumulada(
                            secao,
                            espaco.nomeSecao(),
                            espaco.ordemSecao()
                    )
            );

            acumulada.total++;

            if (estaOcupado(espaco, ocupadosHoje, true)) {
                acumulada.ocupadosHoje++;
            }

            if (estaOcupado(espaco, ocupadosOntem, false)) {
                acumulada.ocupadosOntem++;
            }
        }

        List<OcupacaoAcumulada> acumulados = porSecao.values()
                .stream()
                .sorted((primeira, segunda) ->
                        Integer.compare(primeira.ordem, segunda.ordem)
                )
                .toList();

        long totalEspacos = acumulados.stream()
                .mapToLong(item -> item.total)
                .sum();
        long totalOcupadosHoje = acumulados.stream()
                .mapToLong(item -> item.ocupadosHoje)
                .sum();
        long totalOcupadosOntem = acumulados.stream()
                .mapToLong(item -> item.ocupadosOntem)
                .sum();

        BigDecimal percentualHoje = percentualOcupacao(
                totalOcupadosHoje,
                totalEspacos
        );
        BigDecimal percentualOntem = percentualOcupacao(
                totalOcupadosOntem,
                totalEspacos
        );

        return new OcupacaoDashboard(
                acumulados.stream().map(this::toResponse).toList(),
                indicadorComparavel(percentualHoje, percentualOntem)
        );
    }

    private boolean estaOcupado(
            EspacoOcupacaoProjection espaco,
            Set<String> espacosReservados,
            boolean considerarStatusAtual
    ) {
        boolean statusAtualOcupado = espaco.status() == StatusOcupacao.OCUPADO
                || espaco.status() == StatusOcupacao.RESERVADO;

        return (considerarStatusAtual && statusAtualOcupado)
                || espacosReservados.contains(espaco.espacoId());
    }

    private OcupacaoSecaoResponse toResponse(
            OcupacaoAcumulada acumulada
    ) {
        BigDecimal percentualHoje = percentualOcupacao(
                acumulada.ocupadosHoje,
                acumulada.total
        );
        BigDecimal percentualOntem = percentualOcupacao(
                acumulada.ocupadosOntem,
                acumulada.total
        );

        return new OcupacaoSecaoResponse(
                acumulada.secao,
                acumulada.nome,
                acumulada.total,
                acumulada.ocupadosHoje,
                percentualHoje,
                calcularVariacao(percentualHoje, percentualOntem)
        );
    }

    private BigDecimal percentualOcupacao(long ocupados, long total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(
                    ESCALA_PERCENTUAL,
                    RoundingMode.HALF_UP
            );
        }

        return BigDecimal.valueOf(ocupados)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(total),
                        ESCALA_PERCENTUAL,
                        RoundingMode.HALF_UP
                );
    }

    private List<AcessosPorHoraResponse> preencherTodasAsHoras(
            List<AcessoHoraProjection> acessos
    ) {
        Map<Integer, Long> totaisPorHora = new HashMap<>();
        acessos.forEach(item ->
                totaisPorHora.put(item.hora(), item.total())
        );

        List<AcessosPorHoraResponse> resultado = new ArrayList<>(24);

        for (int hora = 0; hora < 24; hora++) {
            resultado.add(new AcessosPorHoraResponse(
                    hora,
                    totaisPorHora.getOrDefault(hora, 0L)
            ));
        }

        return resultado;
    }

    private IndicadorResponse indicadorComparavel(
            long atual,
            long anterior
    ) {
        return indicadorComparavel(
                BigDecimal.valueOf(atual),
                BigDecimal.valueOf(anterior)
        );
    }

    private IndicadorResponse indicadorComparavel(
            BigDecimal atual,
            BigDecimal anterior
    ) {
        return new IndicadorResponse(
                atual,
                calcularVariacao(atual, anterior),
                true
        );
    }

    private IndicadorResponse indicadorAtual(long valor) {
        return new IndicadorResponse(
                BigDecimal.valueOf(valor),
                null,
                false
        );
    }

    private BigDecimal calcularVariacao(
            BigDecimal atual,
            BigDecimal anterior
    ) {
        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return atual.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO.setScale(
                            ESCALA_PERCENTUAL,
                            RoundingMode.HALF_UP
                    )
                    : BigDecimal.valueOf(100).setScale(
                            ESCALA_PERCENTUAL,
                            RoundingMode.HALF_UP
                    );
        }

        return atual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        anterior.abs(),
                        ESCALA_PERCENTUAL,
                        RoundingMode.HALF_UP
                );
    }

    private static class OcupacaoAcumulada {

        private final TipoSecao secao;
        private final String nome;
        private final int ordem;
        private long total;
        private long ocupadosHoje;
        private long ocupadosOntem;

        private OcupacaoAcumulada(
                TipoSecao secao,
                String nome,
                int ordem
        ) {
            this.secao = secao;
            this.nome = nome;
            this.ordem = ordem;
        }
    }

    private record OcupacaoDashboard(
            List<OcupacaoSecaoResponse> secoes,
            IndicadorResponse indicadorGeral
    ) {
    }
}
