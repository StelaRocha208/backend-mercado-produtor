package br.com.mercadoprodutor.dashboard.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface DashboardQueryRepository {

    long contarAcessosEntre(LocalDateTime inicio, LocalDateTime fim);

    BigDecimal somarReceitaVeiculosEntre(
            LocalDateTime inicio,
            LocalDateTime fim
    );

    BigDecimal somarReceitaEspacosEm(LocalDate data);

    long contarVeiculosPresentesEm(LocalDateTime instante);

    long contarProdutoresInadimplentes();

    List<EspacoOcupacaoProjection> buscarEspacosAtivos();

    Set<String> buscarEspacosReservadosEm(LocalDate data);

    List<AcessoHoraProjection> buscarAcessosPorHora(
            LocalDateTime inicio,
            LocalDateTime fim
    );
}
