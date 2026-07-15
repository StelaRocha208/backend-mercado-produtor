package br.com.mercadoprodutor.portaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;

public interface RegistroRepository extends JpaRepository<Registro, String> {

    Optional<Registro> findByProdutorIdAndStatusRegistro(
            String produtorId,
            StatusRegistro statusRegistro
    );

}