package br.com.mercadoprodutor.reservas.model;

import java.util.Set;

/** Diferencia reservas voláteis de ocupações fixas do mercado. */
public enum TipoReserva {
    DIARIA,
    PRIORITARIA,
    TITULAR,
    PROVISORIA;

    private static final Set<TipoReserva> RESERVAS_VOLATEIS = Set.of(
            DIARIA,
            PRIORITARIA
    );

    private static final Set<TipoReserva> OCUPACOES_FIXAS = Set.of(
            TITULAR,
            PROVISORIA
    );

    public static Set<TipoReserva> reservasVolateis() {
        return RESERVAS_VOLATEIS;
    }

    public static Set<TipoReserva> ocupacoesFixas() {
        return OCUPACOES_FIXAS;
    }
}
