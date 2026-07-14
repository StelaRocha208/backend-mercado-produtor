package br.com.mercadoprodutor.reservas.model;

import java.util.Set;

public enum StatusReserva {
    AGUARDANDO_CONFIRMACAO,
    CONFIRMADA,
    CANCELADA,
    EXPIRADA;

    private static final Set<StatusReserva> STATUS_BLOQUEANTES = Set.of(
            AGUARDANDO_CONFIRMACAO,
            CONFIRMADA
    );

    public static Set<StatusReserva> bloqueantes() {
        return STATUS_BLOQUEANTES;
    }
}
