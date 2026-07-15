package br.com.mercadoprodutor.produtores.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.mercadoprodutor.produtores.model.Produtor;

public interface ProdutorRepository extends JpaRepository<Produtor, String> {

    boolean existsByCpf(String cpf);
    Optional<Produtor> findByCpf(String cpf);
    Optional<Produtor> findByUsuarioId(String usuarioId);

}
