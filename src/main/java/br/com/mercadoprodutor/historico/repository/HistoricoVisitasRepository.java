package br.com.mercadoprodutor.historico.repository;

import br.com.mercadoprodutor.portaria.model.Registro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HistoricoVisitasRepository extends JpaRepository<Registro, String>, JpaSpecificationExecutor<Registro> {

    @EntityGraph(attributePaths = {"produtor", "produtor.usuario"})
    Page<Registro> findAll(Specification<Registro> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"produtor", "produtor.usuario"})
    List<Registro> findAll(Specification<Registro> spec);
}
