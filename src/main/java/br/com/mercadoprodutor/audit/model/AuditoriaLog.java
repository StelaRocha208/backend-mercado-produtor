package br.com.mercadoprodutor.audit.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auditoria_log")
@Getter
@NoArgsConstructor
public class AuditoriaLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(updatable = false, nullable = false)
    private String usuarioId;

    @Column(updatable = false, nullable = false)
    private String acaoExecutada;

    @Column(updatable = false, nullable = false)
    private LocalDateTime dataHora;

    @Column(updatable = false, nullable = false)
    private String enderecoIp;

    public AuditoriaLog(String usuarioId, String acaoExecutada, LocalDateTime dataHora, String enderecoIp) {
        this.usuarioId = usuarioId;
        this.acaoExecutada = acaoExecutada;
        this.dataHora = dataHora;
        this.enderecoIp = enderecoIp;
    }

}
