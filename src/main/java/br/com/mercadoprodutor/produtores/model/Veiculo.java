package br.com.mercadoprodutor.produtores.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo extends BaseEntity {

    @Column(nullable = false, length = 8)
    private String placa;

    @Column(nullable = false, length = 50)
    private String tipo;

    private String marca;
    private String modelo;
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    public Veiculo(String placa, String tipo, String marca, String modelo, String cor, Produtor produtor) {
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.cor = cor;
        this.produtor = produtor;
    }
}
