package br.com.mercadoprodutor.espacos.dto;

import br.com.mercadoprodutor.espacos.model.TipoSecao;

public record OcupacaoResumoResponse(
    TipoSecao secao,
    Integer total,
    Integer livres,
    Integer reservados,
    Integer ocupados,
    Integer indisponiveis
) {}