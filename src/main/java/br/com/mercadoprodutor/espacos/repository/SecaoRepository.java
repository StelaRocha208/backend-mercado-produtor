package br.com.mercadoprodutor.espacos.repository;

import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SecaoRepository extends JpaRepository<Secao, String> {

    Optional<Secao> findByTipo(TipoSecao tipo);

    boolean existsByTipo(TipoSecao tipo);

    List<Secao> findByAtivaTrueOrderByOrdemVisualAsc();
}