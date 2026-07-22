package br.com.mercadoprodutor.reservas.repository;

import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Consulta reservas, conflitos de período e ocupações administrativas.
 */
public interface ReservaRepository extends JpaRepository<Reserva, String> {

    @Query("""
        select count(r) > 0
        from Reserva r
        where r.espaco.id = :espacoId
          and r.statusReserva in :statusBloqueantes
          and r.dataInicio <= :dataFim
          and coalesce(r.dataEncerramento, r.dataFim) >= :dataInicio
    """)
    boolean existsConflitoDePeriodo(
            @Param("espacoId") String espacoId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("statusBloqueantes") Collection<StatusReserva> statusBloqueantes
    );

    @Query("""
        select r.espaco.id
        from Reserva r
        where r.statusReserva in :statusBloqueantes
          and r.tipoReserva in :tiposReserva
          and r.dataInicio <= :dataFim
          and coalesce(r.dataEncerramento, r.dataFim) >= :dataInicio
    """)
    Set<String> findEspacosReservadosNoPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("statusBloqueantes") Collection<StatusReserva> statusBloqueantes,
            @Param("tiposReserva") Collection<TipoReserva> tiposReserva
    );

    @Query("""
        select r
        from Reserva r
        join fetch r.espaco e
        join fetch e.secao s
        join fetch r.produtor p
        join fetch p.usuario
        where p.id = :produtorId
        order by r.dataInicio desc
    """)
    List<Reserva> findByProdutorIdComDetalhes(
            @Param("produtorId") String produtorId
    );

    @Query("""
        select r
        from Reserva r
        join fetch r.espaco e
        join fetch e.secao
        join fetch r.produtor p
        join fetch p.usuario
        left join fetch r.reservaOrigem
        order by r.dataInicio desc
    """)
    List<Reserva> findAllComDetalhes();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select r
        from Reserva r
        where r.id = :id
    """)
    Optional<Reserva> findByIdParaAtualizacao(@Param("id") String id);

    @Query("""
        select r
        from Reserva r
        join fetch r.espaco e
        join fetch e.secao
        join fetch r.produtor p
        join fetch p.usuario
        left join fetch r.reservaOrigem
        where r.id = :id
    """)
    Optional<Reserva> findByIdComDetalhes(@Param("id") String id);
}
