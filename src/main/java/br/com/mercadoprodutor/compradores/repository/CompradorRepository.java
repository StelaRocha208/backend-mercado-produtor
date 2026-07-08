package br.com.mercadoprodutor.compradores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.mercadoprodutor.compradores.model.Comprador;
import java.util.Optional;

public interface CompradorRepository extends JpaRepository<Comprador, String> {
    boolean existsByCpf(String cpf);

    Optional<Comprador> findByUsuarioId(String usuarioId);
}
