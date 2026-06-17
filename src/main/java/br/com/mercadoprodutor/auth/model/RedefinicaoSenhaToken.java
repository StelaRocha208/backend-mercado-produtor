package br.com.mercadoprodutor.auth.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "redefinicao_senha_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RedefinicaoSenhaToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    public RedefinicaoSenhaToken(String token, Usuario usuario, LocalDateTime dataExpiracao) {
        this.token = token;
        this.usuario = usuario;
        this.dataExpiracao = dataExpiracao;
    }

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }
}
