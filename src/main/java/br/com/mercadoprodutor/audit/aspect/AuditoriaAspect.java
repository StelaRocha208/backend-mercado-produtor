package br.com.mercadoprodutor.audit.aspect;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.mercadoprodutor.audit.model.AuditoriaLog;
import br.com.mercadoprodutor.audit.repository.AuditoriaLogRepository;
import jakarta.servlet.http.HttpServletRequest;



@Aspect
@Component
public class AuditoriaAspect {
    private final AuditoriaLogRepository repository;

    public AuditoriaAspect(AuditoriaLogRepository repository) {
        this.repository = repository;
    }

    // Intercepta qualquer metodo que tenha a anotação @AuditarAcao
    @AfterReturning("@annotation(auditarAcao)")
    public void registrarLog(JoinPoint joinPoint, AuditarAcao auditarAcao) {
        
        String acao = auditarAcao.valor();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();

        String usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        AuditoriaLog log = new AuditoriaLog(usuarioLogado, acao, LocalDateTime.now(), ip);
        repository.save(log);
    }
}
