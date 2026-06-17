package br.com.mercadoprodutor.audit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.mercadoprodutor.audit.model.AuditoriaLog;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, String>{
}
