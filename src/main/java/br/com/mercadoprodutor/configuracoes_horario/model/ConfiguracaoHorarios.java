package br.com.mercadoprodutor.configuracoes_horario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracoes_horarios")
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracaoHorarios {

    @Id
    @Column(name = "id")
    private String id = "DEFAULT";

    // Dias de funcionamento
    @Column(name = "dia_dom")
    private Boolean diaDom = false;

    @Column(name = "dia_seg")
    private Boolean diaSeg = false;

    @Column(name = "dia_ter")
    private Boolean diaTer = true;

    @Column(name = "dia_qua")
    private Boolean diaQua = true;

    @Column(name = "dia_qui")
    private Boolean diaQui = true;

    @Column(name = "dia_sex")
    private Boolean diaSex = true;

    @Column(name = "dia_sab")
    private Boolean diaSab = false;

    // Horários de funcionamento
    @Column(name = "horario_comprador_inicio")
    private String horarioCompradorInicio = "05:00";

    @Column(name = "horario_comprador_fim")
    private String horarioCompradorFim = "13:00";

    @Column(name = "horario_produtor_inicio")
    private String horarioProdutorInicio = "04:00";

    @Column(name = "horario_produtor_fim")
    private String horarioProdutorFim = "14:00";

    // Regras de reserva
    @Column(name = "prazo_antecedencia_dias")
    private Integer prazoAntecedenciaDias = 7;

    @Column(name = "expiracao_reserva_horas")
    private Integer expiracaoReservaHoras = 2;

    @Column(name = "lembrete_reserva_horas")
    private Integer lembreteReservaHoras = 24;

}
