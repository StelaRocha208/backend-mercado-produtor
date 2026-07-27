package br.com.mercadoprodutor.historico.model;

public enum StatusPagamento {
    PAGO("Pago"),
    PENDENTE("Pendente"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
