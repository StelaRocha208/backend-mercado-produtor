package br.com.mercadoprodutor.dashboard.dto;

import br.com.mercadoprodutor.espacos.model.TipoSecao;

import java.math.BigDecimal;

public record OcupacaoSecaoResponse(
        TipoSecao secao,
        String nome,
        long totalEspacos,
        long espacosOcupados,
        BigDecimal percentual,
        BigDecimal variacaoPercentual
) {
}
