package br.com.mercadoprodutor.portaria.service;

import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;

public interface IRegistroService {

    RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto);

}
