package br.com.mercadoprodutor.produtores.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.mercadoprodutor.produtores.model.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {

    Optional<Veiculo> findByPlaca(String placa);

}