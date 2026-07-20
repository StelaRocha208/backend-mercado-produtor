package br.com.mercadoprodutor.portaria.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.model.Veiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registros")
@Getter
@Setter
@NoArgsConstructor
public class Registro extends BaseEntity {

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_registro", nullable = false)
    private StatusRegistro statusRegistro;

    /*
     * TODO
     * Estes campos serão migrados para a entidade Reserva
     * quando o módulo de Reserva de Espaço for implementado.
     */
    @Column(name = "numero_nf")
    private String numeroNF;

    @Column(length = 500)
    private String observacao;

    @Column
    private String secao;

    @Column
    private String espaco;

    @Column(name = "taxa_veiculo", precision = 10, scale = 2)
    private BigDecimal taxaVeiculo;

    /*
     * Liberação excepcional registrada no histórico da visita.
     */

    @Column(name = "liberacao_excepcional", nullable = false)
    private Boolean liberacaoExcepcional = false;

    @Column(name = "justificativa_liberacao", length = 1000)
    private String justificativaLiberacao;

    @Column(name = "data_liberacao")
    private LocalDateTime dataLiberacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
}