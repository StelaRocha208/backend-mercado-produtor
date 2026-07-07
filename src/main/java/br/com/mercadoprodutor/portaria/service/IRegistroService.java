package br.com.mercadoprodutor.portaria.service;

import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
import br.com.mercadoprodutor.portaria.dto.BuscaPortariaResponseDTO;

public interface IRegistroService {

    RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto);
    BuscaPortariaResponseDTO buscarUsuario(String valor);
}