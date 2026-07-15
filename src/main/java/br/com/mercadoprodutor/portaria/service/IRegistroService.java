package br.com.mercadoprodutor.portaria.service;

import br.com.mercadoprodutor.portaria.dto.BuscaPortariaResponseDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroSaidaDTO;
import br.com.mercadoprodutor.portaria.dto.LiberacaoExcepcionalDTO;

public interface IRegistroService {

    BuscaPortariaResponseDTO buscarUsuario(String valor);
    RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto);
    RegistroResponseDTO registrarSaida(RegistroSaidaDTO dto);
    void liberarAcesso(LiberacaoExcepcionalDTO dto);

}