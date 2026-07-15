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

import java.time.LocalDate;

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

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reserva", nullable = false, length = 40)
    @Builder.Default
    private StatusReserva statusReserva = StatusReserva.AGUARDANDO_CONFIRMACAO;

    @Column(length = 500)
    private String observacao;

    public static Reserva criar(
            Produtor produtor,
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim,
            String observacao
    ) {
        return Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .observacao(observacao)
                .build();
    }
}
