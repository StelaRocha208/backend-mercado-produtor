package br.com.mercadoprodutor.portaria.dto;

import java.time.LocalDateTime;

import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;

public record RegistroResponseDTO(
        String id,
        String produtorId,
        String veiculoId,
        String secao,
        String espaco,
        String numeroNF,
        String observacao,
        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,
        StatusRegistro statusRegistro
) {

    public RegistroResponseDTO(Registro registro) {
        this(
                registro.getId(),
                registro.getProdutor().getId(),
                registro.getVeiculo().getId(),
                registro.getSecao(),
                registro.getEspaco(),
                registro.getNumeroNF(),
                registro.getObservacao(),
                registro.getDataEntrada(),
                registro.getDataSaida(),
                registro.getStatusRegistro()
        );
    }
}
