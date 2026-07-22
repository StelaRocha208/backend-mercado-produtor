package br.com.mercadoprodutor.reservas.model;

import java.util.Set;

public enum StatusReserva {
    AGUARDANDO_CONFIRMACAO,
    CONFIRMADA,
    ENCERRADA,
    CANCELADA,
    EXPIRADA;

    private static final Set<StatusReserva> STATUS_BLOQUEANTES = Set.of(
            AGUARDANDO_CONFIRMACAO,
            CONFIRMADA
    );

    private static final Set<StatusReserva> STATUS_FINALIZADOS = Set.of(
            ENCERRADA,
            CANCELADA,
            EXPIRADA
    );

    public static Set<StatusReserva> bloqueantes() {
        return STATUS_BLOQUEANTES;
    }

    public boolean finalizado() {
        return STATUS_FINALIZADOS.contains(this);
    }
}
