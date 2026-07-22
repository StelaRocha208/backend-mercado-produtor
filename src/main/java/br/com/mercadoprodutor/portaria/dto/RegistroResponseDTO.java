package br.com.mercadoprodutor.portaria.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;

public record RegistroResponseDTO(

        String id,
        String usuarioId,
        String nome,
        String perfil,
        String veiculoId,
        String placa,
        BigDecimal taxaVeiculo,

        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,

        StatusRegistro statusRegistro,

        Boolean liberacaoExcepcional,
        String justificativaLiberacao,
        LocalDateTime dataLiberacao

) {

    public RegistroResponseDTO(
            Registro registro,
            String perfil
    ) {

        this(

                registro.getId(),
                registro.getProdutor().getUsuario().getId(),
                registro.getProdutor().getUsuario().getNome(),
                perfil,
                registro.getVeiculo().getId(),
                registro.getVeiculo().getPlaca(),
                registro.getTaxaVeiculo(),

                registro.getReserva() != null ? registro.getReserva().getId() : null,
                registro.getSecao(),
                registro.getEspaco(),

                registro.getDataEntrada(),
                registro.getDataSaida(),

                registro.getStatusRegistro(),

                registro.getLiberacaoExcepcional(),
                registro.getJustificativaLiberacao(),
                registro.getDataLiberacao()

        );
    }

}