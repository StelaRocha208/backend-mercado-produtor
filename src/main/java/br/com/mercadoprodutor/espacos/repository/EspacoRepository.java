package br.com.mercadoprodutor.espacos.repository;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EspacoRepository extends JpaRepository<Espaco, String> {

    boolean existsBySecaoAndNumero(Secao secao, String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e
        from Espaco e
        join fetch e.secao
        where e.id = :id
    """)
    Optional<Espaco> findByIdParaAtualizacao(@Param("id") String id);

    @Query("""
        select e
        from Espaco e
        join fetch e.secao s
        where e.ativo = true
        order by s.ordemVisual asc, e.ordemVisual asc
    """)
    List<Espaco> findAllAtivosOrdenados();

    @Query("""
        select e
        from Espaco e
        join fetch e.secao s
        where e.ativo = true
          and s.tipo = :tipoSecao
        order by e.ordemVisual asc
    """)
    List<Espaco> findAtivosByTipoSecao(@Param("tipoSecao") TipoSecao tipoSecao);

    @Modifying(clearAutomatically = true)
    @Query("""
        update Espaco e
        set e.areaM2 = :areaM2
        where e.secao.id = :secaoId
          and e.ativo = true
    """)
    int atualizarAreaM2PorSecao(
            @Param("secaoId") String secaoId,
            @Param("areaM2") BigDecimal areaM2
    );
}
