package br.com.mercadoprodutor.configuracoes_horario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracaoHorariosDTO {
    private Boolean diaDom;
    private Boolean diaSeg;
    private Boolean diaTer;
    private Boolean diaQua;
    private Boolean diaQui;
    private Boolean diaSex;
    private Boolean diaSab;

    private String horarioCompradorInicio;
    private String horarioCompradorFim;
    private String horarioProdutorInicio;
    private String horarioProdutorFim;

    private Integer prazoAntecedenciaDias;
    private Integer expiracaoReservaHoras;
    private Integer lembreteReservaHoras;

}