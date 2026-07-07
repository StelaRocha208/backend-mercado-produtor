package br.com.mercadoprodutor.portaria.dto;

import java.time.LocalDateTime;

import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;

public record RegistroResponseDTO(

        String id,
        String usuarioId,
        String nome,
        String perfil,
        String veiculoId,
        String placa,
        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,
        StatusRegistro statusRegistro

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
                registro.getDataEntrada(),
                registro.getDataSaida(),
                registro.getStatusRegistro()

        );
    }

}