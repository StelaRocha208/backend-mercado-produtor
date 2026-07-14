package br.com.mercadoprodutor.reservas.repository;

import br.com.mercadoprodutor.produtores.model.Produtor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProdutorReservaRepository extends Repository<Produtor, String> {

    @Query("""
        select p
        from Produtor p
        join fetch p.usuario u
        where u.id = :usuarioId
    """)
    Optional<Produtor> findByUsuarioId(@Param("usuarioId") String usuarioId);
}
