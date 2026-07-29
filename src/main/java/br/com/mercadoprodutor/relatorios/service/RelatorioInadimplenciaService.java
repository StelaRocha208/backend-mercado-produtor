package br.com.mercadoprodutor.relatorios.service;

import br.com.mercadoprodutor.relatorios.dto.ProdutorInadimplenteDTO;
import br.com.mercadoprodutor.relatorios.dto.RelatorioInadimplenciaFiltroDTO;
import br.com.mercadoprodutor.relatorios.dto.ResumoInadimplenciaDTO;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioInadimplenciaService {

    private final ReservaRepository reservaRepository;


    @Transactional(readOnly = true)
    public List<ProdutorInadimplenteDTO> gerarRelatorio(
            RelatorioInadimplenciaFiltroDTO filtro
    ) {

        List<Reserva> reservas =
                reservaRepository.findReservasProdutoresInadimplentes();


        Map<String, List<Reserva>> reservasPorProdutor =
                reservas.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getProdutor().getId()
                        ));


        List<ProdutorInadimplenteDTO> filtrados =
                reservasPorProdutor.values()
                        .stream()
                        .map(this::converterParaDTO)
                        .filter(dto -> aplicarFiltros(dto, filtro))
                        .sorted(Comparator.comparing(ProdutorInadimplenteDTO::nome))
                        .toList();

        return aplicarPaginacao(filtrados, filtro);
    }


    @Transactional(readOnly = true)
    public ResumoInadimplenciaDTO gerarResumo() {

        BigDecimal total =
                reservaRepository.calcularTotalEmAberto();


        Long quantidade =
                reservaRepository.contarProdutoresInadimplentes();


        BigDecimal media = BigDecimal.ZERO;


        if (quantidade > 0) {
            media = total.divide(
                    BigDecimal.valueOf(quantidade),
                    2,
                    RoundingMode.HALF_UP
            );
        }


        return new ResumoInadimplenciaDTO(
                total,
                quantidade,
                media
        );
    }


    private ProdutorInadimplenteDTO converterParaDTO(
            List<Reserva> reservas
    ) {

        Reserva primeira = reservas.get(0);


        BigDecimal valorTotal =
                reservas.stream()
                        .map(Reserva::getValorTaxaSolo)
                        .filter(valor -> valor != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        LocalDate dataUltimaVisita =
                reservas.stream()
                        .map(Reserva::getDataInicio)
                        .filter(data -> data != null)
                        .max(LocalDate::compareTo)
                        .orElse(null);


        return new ProdutorInadimplenteDTO(
                primeira.getProdutor().getId(),
                primeira.getProdutor()
                        .getUsuario()
                        .getNome(),
                primeira.getProdutor()
                        .getCpf(),
                primeira.getProdutor()
                        .getTelefone(),
                valorTotal,
                dataUltimaVisita
        );
    }


    private boolean aplicarFiltros(
            ProdutorInadimplenteDTO dto,
            RelatorioInadimplenciaFiltroDTO filtro
    ) {

        if (filtro == null) {
            return true;
        }


        if (filtro.nome() != null
                && !filtro.nome().isBlank()
                && !dto.nome()
                .toLowerCase()
                .contains(
                        filtro.nome()
                                .toLowerCase()
                )) {

            return false;
        }


        if (filtro.cpf() != null
                && !filtro.cpf().isBlank()
                && !dto.cpf()
                .contains(filtro.cpf())) {

            return false;
        }


        if (filtro.valorMinimo() != null
                && dto.valorEmAberto()
                .compareTo(filtro.valorMinimo()) < 0) {

            return false;
        }


        if (filtro.valorMaximo() != null
                && dto.valorEmAberto()
                .compareTo(filtro.valorMaximo()) > 0) {

            return false;
        }


        if (filtro.dataVisitaDe() != null
                && (dto.dataUltimaVisitaEmAberto() == null
                || dto.dataUltimaVisitaEmAberto().isBefore(filtro.dataVisitaDe()))) {

            return false;
        }


        if (filtro.dataVisitaAte() != null
                && (dto.dataUltimaVisitaEmAberto() == null
                || dto.dataUltimaVisitaEmAberto().isAfter(filtro.dataVisitaAte()))) {

            return false;
        }


        return true;
    }


    private List<ProdutorInadimplenteDTO> aplicarPaginacao(
            List<ProdutorInadimplenteDTO> lista,
            RelatorioInadimplenciaFiltroDTO filtro
    ) {

        if (filtro == null
                || filtro.pagina() == null
                || filtro.tamanho() == null
                || filtro.tamanho() <= 0) {

            return lista;
        }


        int pagina = Math.max(filtro.pagina(), 0);
        int inicio = pagina * filtro.tamanho();

        if (inicio >= lista.size()) {
            return List.of();
        }

        int fim = Math.min(inicio + filtro.tamanho(), lista.size());

        return lista.subList(inicio, fim);
    }
}