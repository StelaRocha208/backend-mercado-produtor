package br.com.mercadoprodutor.dashboard.repository;

import br.com.mercadoprodutor.reservas.model.StatusReserva;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DashboardQueryRepositoryJpa implements DashboardQueryRepository {

    private final EntityManager entityManager;

    @Override
    public long contarAcessosEntre(
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        return entityManager.createQuery("""
                select count(r)
                from Registro r
                where r.dataEntrada >= :inicio
                  and r.dataEntrada < :fim
                """, Long.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
    }

    @Override
    public BigDecimal somarReceitaVeiculosEntre(
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        BigDecimal resultado = entityManager.createQuery("""
                select sum(r.taxaVeiculo)
                from Registro r
                where r.dataEntrada >= :inicio
                  and r.dataEntrada < :fim
                """, BigDecimal.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();

        return resultado == null ? BigDecimal.ZERO : resultado;
    }

    @Override
    public BigDecimal somarReceitaEspacosEm(LocalDate data) {
        BigDecimal resultado = entityManager.createQuery("""
                select sum(
                    coalesce(r.areaM2Cobrada, e.areaM2)
                    * coalesce(r.tarifaPorM2Aplicada, s.taxaPorM2)
                )
                from Reserva r
                join r.espaco e
                join e.secao s
                where r.statusReserva in :statusGeradoresReceita
                  and r.dataInicio <= :data
                  and coalesce(r.dataEncerramento, r.dataFim) >= :data
                  and coalesce(r.areaM2Cobrada, e.areaM2) is not null
                  and coalesce(r.tarifaPorM2Aplicada, s.taxaPorM2) is not null
                """, BigDecimal.class)
                .setParameter(
                        "statusGeradoresReceita",
                        Set.of(
                                StatusReserva.CONFIRMADA,
                                StatusReserva.ENCERRADA
                        )
                )
                .setParameter("data", data)
                .getSingleResult();

        return resultado == null ? BigDecimal.ZERO : resultado;
    }

    @Override
    public long contarVeiculosPresentesEm(LocalDateTime instante) {
        return entityManager.createQuery("""
                select count(distinct r.veiculo.id)
                from Registro r
                where r.dataEntrada <= :instante
                  and (r.dataSaida is null or r.dataSaida > :instante)
                """, Long.class)
                .setParameter("instante", instante)
                .getSingleResult();
    }

    @Override
    public long contarProdutoresInadimplentes() {
        return entityManager.createQuery("""
                select count(p)
                from Produtor p
                where p.inadimplente = true
                """, Long.class)
                .getSingleResult();
    }

    @Override
    public List<EspacoOcupacaoProjection> buscarEspacosAtivos() {
        return entityManager.createQuery("""
                select new br.com.mercadoprodutor.dashboard.repository.EspacoOcupacaoProjection(
                    e.id,
                    s.tipo,
                    s.nome,
                    s.ordemVisual,
                    e.statusOcupacao
                )
                from Espaco e
                join e.secao s
                where e.ativo = true
                  and s.ativa = true
                order by s.ordemVisual, e.ordemVisual
                """, EspacoOcupacaoProjection.class)
                .getResultList();
    }

    @Override
    public Set<String> buscarEspacosReservadosEm(LocalDate data) {
        List<String> ids = entityManager.createQuery("""
                select distinct r.espaco.id
                from Reserva r
                where r.statusReserva in :statusBloqueantes
                  and r.dataInicio <= :data
                  and coalesce(r.dataEncerramento, r.dataFim) >= :data
                """, String.class)
                .setParameter(
                        "statusBloqueantes",
                        StatusReserva.bloqueantes()
                )
                .setParameter("data", data)
                .getResultList();

        return new HashSet<>(ids);
    }

    @Override
    public List<AcessoHoraProjection> buscarAcessosPorHora(
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        List<Object[]> resultados = entityManager.createQuery("""
                select hour(r.dataEntrada), count(r)
                from Registro r
                where r.dataEntrada >= :inicio
                  and r.dataEntrada < :fim
                group by hour(r.dataEntrada)
                order by hour(r.dataEntrada)
                """, Object[].class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        return resultados.stream()
                .map(resultado -> new AcessoHoraProjection(
                        ((Number) resultado[0]).intValue(),
                        ((Number) resultado[1]).longValue()
                ))
                .toList();
    }
}
