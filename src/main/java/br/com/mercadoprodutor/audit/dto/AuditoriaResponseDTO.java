package br.com.mercadoprodutor.audit.dto;

import java.time.LocalDateTime;
import br.com.mercadoprodutor.audit.model.AuditoriaLog;

public record AuditoriaResponseDTO(
        String id,
        String usuarioId,
        String acaoExecutada,
        LocalDateTime dataHora,
        String enderecoIp
) {
    public AuditoriaResponseDTO(AuditoriaLog auditoria) {
        this(
            auditoria.getId(),
            auditoria.getUsuarioId(),
            auditoria.getAcaoExecutada(),
            auditoria.getDataHora(),
            auditoria.getEnderecoIp()
        );
    }
}