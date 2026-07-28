package br.com.mercadoprodutor.relatorio.visitas.service;

import java.util.List;

import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioFiltroDTO;
import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioVisitaDTO;

public interface IRelatorioService {

    List<RelatorioVisitaDTO> gerarRelatorio(RelatorioFiltroDTO filtro);

}
