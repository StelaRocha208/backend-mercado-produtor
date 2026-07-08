package br.com.mercadoprodutor.espacos.dto;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;

public record EspacoResponse(
    String id,
    String numero,
    String secaoId,
    String secaoNome,
    TipoSecao secaoTipo,
    StatusOcupacao statusOcupacao,
    Boolean ativo,
    String pavilhao,
    String grupoVisual,
    Integer linha,
    Integer coluna,
    Integer ordemVisual,
    Boolean selecionavel,
    String motivoBloqueio
) {
    public static EspacoResponse fromEntity(Espaco espaco) {
        boolean selecionavel =
            Boolean.TRUE.equals(espaco.getAtivo())
                && espaco.getStatusOcupacao() == StatusOcupacao.LIVRE;

        String motivoBloqueio = null;

        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            motivoBloqueio = "Espaço inativo.";
        } else if (espaco.getStatusOcupacao() == StatusOcupacao.RESERVADO) {
            motivoBloqueio = "Espaço reservado.";
        } else if (espaco.getStatusOcupacao() == StatusOcupacao.OCUPADO) {
            motivoBloqueio = "Espaço ocupado.";
        } else if (espaco.getStatusOcupacao() == StatusOcupacao.INDISPONIVEL) {
            motivoBloqueio = "Espaço indisponível.";
        }

        return new EspacoResponse(
            espaco.getId(),
            espaco.getNumero(),
            espaco.getSecao().getId(),
            espaco.getSecao().getNome(),
            espaco.getSecao().getTipo(),
            espaco.getStatusOcupacao(),
            espaco.getAtivo(),
            espaco.getPavilhao(),
            espaco.getGrupoVisual(),
            espaco.getLinha(),
            espaco.getColuna(),
            espaco.getOrdemVisual(),
            selecionavel,
            motivoBloqueio
        );
    }
}