package br.com.mercadoprodutor.compradores.service;

import br.com.mercadoprodutor.compradores.dto.CompradorCreateDTO;

public interface ICompradorService {
    void criarComprador(CompradorCreateDTO dto);
}