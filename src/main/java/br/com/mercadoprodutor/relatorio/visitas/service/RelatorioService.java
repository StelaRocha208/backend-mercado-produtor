package br.com.mercadoprodutor.relatorio.visitas.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioFiltroDTO;
import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioVisitaDTO;
import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService implements IRelatorioService {

    private final RegistroRepository registroRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RelatorioVisitaDTO> gerarRelatorio(RelatorioFiltroDTO filtro) {

        List<Registro> registros =
        registroRepository.buscarRelatorio(
                filtro.dataInicio().atStartOfDay(),
                filtro.dataFim().atTime(23, 59, 59),
                filtro.secao()      
        );

       if (filtro.produtor() != null && !filtro.produtor().isBlank()) {

    registros = registros.stream()
            .filter(r -> r.getProdutor()
                    .getUsuario()
                    .getNome()
                    .toLowerCase()
                    .contains(filtro.produtor().toLowerCase()))
            .toList();
}

        return registros.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private RelatorioVisitaDTO converterParaDTO(Registro registro) {

        BigDecimal taxaSolo = BigDecimal.ZERO;

        if (registro.getReserva() != null
                && registro.getReserva().getValorTaxaSolo() != null) {

            taxaSolo = registro.getReserva().getValorTaxaSolo();
        }

        BigDecimal taxaVeiculo =
                registro.getTaxaVeiculo() != null
                        ? registro.getTaxaVeiculo()
                        : BigDecimal.ZERO;

        BigDecimal valorTotal = taxaVeiculo.add(taxaSolo);

        return new RelatorioVisitaDTO(

                registro.getProdutor().getUsuario().getNome(),

                registro.getVeiculo().getPlaca(),
                registro.getVeiculo().getTipo(),

                registro.getDataEntrada(),
                registro.getDataSaida(),

                registro.getSecao(),
                registro.getEspaco(),

                taxaVeiculo,
                taxaSolo,
                valorTotal
        );
    }
}