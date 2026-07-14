package br.com.mercadoprodutor.reservas.mapper;

import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.model.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),

                reserva.getProdutor().getId(),
                reserva.getProdutor().getUsuario().getNome(),

                reserva.getEspaco().getId(),
                reserva.getEspaco().getNumero(),
                reserva.getEspaco().getSecao().getNome(),
                reserva.getEspaco().getSecao().getTipo(),

                reserva.getDataInicio(),
                reserva.getDataFim(),

                reserva.getStatusReserva(),

                reserva.getObservacao(),

                reserva.getDataCriacao()
        );
    }
}
