package br.com.mercadoprodutor.configuracoes_horario.repository;

import br.com.mercadoprodutor.configuracoes_horario.model.ConfiguracaoHorarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoHorariosRepository extends JpaRepository<ConfiguracaoHorarios, String> {

}
