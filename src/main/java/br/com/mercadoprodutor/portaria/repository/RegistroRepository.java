package br.com.mercadoprodutor.portaria.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;

public interface RegistroRepository extends JpaRepository<Registro, String> {

    Optional<Registro> findByProdutorIdAndStatusRegistro(
            String produtorId,
            StatusRegistro statusRegistro
    );

    @Query("""
        SELECT r
        FROM Registro r
        WHERE r.dataEntrada BETWEEN :dataInicio AND :dataFim
          AND (:secao IS NULL OR r.secao = :secao)
        ORDER BY r.dataEntrada DESC
    """)
    List<Registro> buscarRelatorio(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("secao") String secao
    );

}