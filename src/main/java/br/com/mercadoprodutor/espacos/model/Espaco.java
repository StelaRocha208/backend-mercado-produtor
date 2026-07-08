package br.com.mercadoprodutor.espacos.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "espacos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_espaco_secao_numero",
            columnNames = {"secao_id", "numero"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Espaco extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_ocupacao", nullable = false, length = 30)
    private StatusOcupacao statusOcupacao = StatusOcupacao.LIVRE;

    @Column(name = "area_m2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "secao_id", nullable = false)
    private Secao secao;



    //Não estão no diagrama de classe, mas são necessários para o front-end
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(length = 80)
    private String pavilhao;

    @Column(name = "grupo_visual", length = 80)
    private String grupoVisual;

    private Integer linha;

    private Integer coluna;

    @Column(name = "ordem_visual", nullable = false)
    private Integer ordemVisual;

    public Espaco(
        String numero,
        Secao secao,
        BigDecimal areaM2,
        String pavilhao,
        String grupoVisual,
        Integer linha,
        Integer coluna,
        Integer ordemVisual
    ) {
        this.numero = numero;
        this.secao = secao;
        this.areaM2 = areaM2;
        this.pavilhao = pavilhao;
        this.grupoVisual = grupoVisual;
        this.linha = linha;
        this.coluna = coluna;
        this.ordemVisual = ordemVisual;
        this.statusOcupacao = StatusOcupacao.LIVRE;
        this.ativo = true;
    }
}