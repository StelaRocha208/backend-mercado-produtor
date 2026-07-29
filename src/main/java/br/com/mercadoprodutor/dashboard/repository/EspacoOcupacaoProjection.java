package br.com.mercadoprodutor.dashboard.repository;

import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;

public record EspacoOcupacaoProjection(
        String espacoId,
        TipoSecao secao,
        String nomeSecao,
        int ordemSecao,
        StatusOcupacao status
) {
}
