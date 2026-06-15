package br.com.mercadoprodutor.produtores.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtores")
@Getter
@Setter
@NoArgsConstructor
public class Produtor extends BaseEntity {

    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    // Buscar um produtor não traz o Usuario inteiro na query a menos que você chame produtor.getUsuario().getNome()
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "produtor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    public Produtor(String cpf, String telefone, Usuario usuario) {
        this.cpf = cpf;
        this.telefone = telefone;
        this.usuario = usuario;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        veiculos.add(veiculo);
        veiculo.setProdutor(this);
    }
}
