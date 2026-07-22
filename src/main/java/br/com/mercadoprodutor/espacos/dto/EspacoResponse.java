package br.com.mercadoprodutor.espacos.dto;

import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;

import java.math.BigDecimal;

public record EspacoResponse(
        String id,
        String numero,
        String secaoId,
        String secaoNome,
        TipoSecao secaoTipo,
        BigDecimal areaM2,
        StatusOcupacao statusOcupacao,
        Boolean ativo,
        String pavilhao,
        String grupoVisual,
        Integer linha,
        Integer coluna,
        Integer ordemVisual,
        Boolean selecionavel,
        String motivoBloqueio,
        Boolean selecionavelAdministracao,
        String motivoBloqueioAdministracao
) {
}
