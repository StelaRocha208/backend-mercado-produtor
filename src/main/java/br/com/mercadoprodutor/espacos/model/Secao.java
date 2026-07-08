package br.com.mercadoprodutor.espacos.model;

import java.math.BigDecimal;

import br.com.mercadoprodutor.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "secoes")
@Getter
@Setter
@NoArgsConstructor
public class Secao extends BaseEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(name = "taxa_por_m2", precision = 10, scale = 2)
    private BigDecimal taxaPorM2;

    //O campo tipo para armazenar o tipo da seção (se é box, pedra, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private TipoSecao tipo;

    //Não estão no diagrama de classe, mas são necessários para o front-end
    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "ordem_visual", nullable = false)
    private Integer ordemVisual;

  

    public Secao(String nome, TipoSecao tipo, Integer capacidade, Integer ordemVisual) {
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.ordemVisual = ordemVisual;
        this.ativa = true;
    }
}