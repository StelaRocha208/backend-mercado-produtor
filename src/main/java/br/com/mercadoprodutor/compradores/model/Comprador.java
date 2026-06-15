package br.com.mercadoprodutor.compradores.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compradores")
@Getter
@Setter
@NoArgsConstructor
public class Comprador extends BaseEntity {

    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Comprador(String cpf, String telefone, Usuario usuario) {
        this.cpf = cpf;
        this.telefone = telefone;
        this.usuario = usuario;
    }
}