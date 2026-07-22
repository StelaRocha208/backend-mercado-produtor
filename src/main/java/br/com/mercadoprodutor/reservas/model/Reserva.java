package br.com.mercadoprodutor.reservas.model;

import br.com.mercadoprodutor.core.model.BaseEntity;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.produtores.model.Produtor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa reservas diárias e vínculos administrativos de um espaço.
 */
@Entity
@Table(name = "reservas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_origem_id")
    private Reserva reservaOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reserva", nullable = false, length = 30)
    @Builder.Default
    private TipoReserva tipoReserva = TipoReserva.DIARIA;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "data_encerramento")
    private LocalDate dataEncerramento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reserva", nullable = false, length = 40)
    @Builder.Default
    private StatusReserva statusReserva = StatusReserva.AGUARDANDO_CONFIRMACAO;

    @Column(length = 500)
    private String observacao;

    @Column(name = "motivo_encerramento", length = 500)
    private String motivoEncerramento;

    @Column(name = "area_m2_cobrada", precision = 10, scale = 2)
    private BigDecimal areaM2Cobrada;

    @Column(name = "tarifa_por_m2_aplicada", precision = 10, scale = 2)
    private BigDecimal tarifaPorM2Aplicada;

    @Column(name = "dias_uso")
    private Integer diasUso;

    @Column(name = "valor_taxa_solo", precision = 14, scale = 2)
    private BigDecimal valorTaxaSolo;

    public static Reserva criarDiaria(
            Produtor produtor,
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim,
            String observacao
    ) {
        return Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .tipoReserva(TipoReserva.DIARIA)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .observacao(observacao)
                .build();
    }

    public static Reserva criarTitular(
            Produtor produtor,
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim,
            String observacao
    ) {
        return Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .tipoReserva(TipoReserva.TITULAR)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .statusReserva(StatusReserva.CONFIRMADA)
                .observacao(observacao)
                .build();
    }

    public static Reserva criarPrioritaria(
            Produtor produtor,
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim,
            String observacao
    ) {
        return Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .tipoReserva(TipoReserva.PRIORITARIA)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .statusReserva(StatusReserva.CONFIRMADA)
                .observacao(observacao)
                .build();
    }

    public static Reserva criarProvisoria(
            Produtor produtor,
            Espaco espaco,
            Reserva reservaOrigem,
            LocalDate dataInicio,
            LocalDate dataFim,
            String observacao
    ) {
        return Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .reservaOrigem(reservaOrigem)
                .tipoReserva(TipoReserva.PROVISORIA)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .statusReserva(StatusReserva.CONFIRMADA)
                .observacao(observacao)
                .build();
    }

    public void encerrar(LocalDate dataEncerramento, String motivo) {
        this.dataEncerramento = dataEncerramento;
        this.motivoEncerramento = motivo;
        calcularTaxaSolo(dataEncerramento);
        this.statusReserva = StatusReserva.ENCERRADA;
    }

    public void confirmar() {
        this.statusReserva = StatusReserva.CONFIRMADA;
    }

    public void cancelar(String motivo) {
        this.dataEncerramento = null;
        this.motivoEncerramento = motivo;
        this.statusReserva = StatusReserva.CANCELADA;
    }

    private void calcularTaxaSolo(LocalDate dataFimUso) {
        if (espaco == null || espaco.getSecao() == null) {
            return;
        }

        BigDecimal areaM2 = espaco.getAreaM2();
        BigDecimal tarifaPorM2 = espaco.getSecao().getTaxaPorM2();

        if (areaM2 == null || tarifaPorM2 == null) {
            return;
        }

        int quantidadeDias = Math.toIntExact(
                ChronoUnit.DAYS.between(dataInicio, dataFimUso) + 1
        );

        this.areaM2Cobrada = areaM2.setScale(2, RoundingMode.HALF_UP);
        this.tarifaPorM2Aplicada = tarifaPorM2.setScale(
                2,
                RoundingMode.HALF_UP
        );
        this.diasUso = quantidadeDias;
        this.valorTaxaSolo = areaM2
                .multiply(tarifaPorM2)
                .multiply(BigDecimal.valueOf(quantidadeDias))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
