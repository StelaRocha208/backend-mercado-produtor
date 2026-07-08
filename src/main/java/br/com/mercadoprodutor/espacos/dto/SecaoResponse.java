package br.com.mercadoprodutor.espacos.dto;

import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;

public record SecaoResponse(
    String id,
    String nome,
    TipoSecao tipo,
    Integer capacidade,
    Boolean ativa,
    Integer ordemVisual
) {
    public static SecaoResponse fromEntity(Secao secao) {
        return new SecaoResponse(
            secao.getId(),
            secao.getNome(),
            secao.getTipo(),
            secao.getCapacidade(),
            secao.getAtiva(),
            secao.getOrdemVisual()
        );
    }
}