package br.com.mercadoprodutor.espacos.repository;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EspacoRepository extends JpaRepository<Espaco, String> {

    boolean existsBySecaoAndNumero(Secao secao, String numero);

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
}