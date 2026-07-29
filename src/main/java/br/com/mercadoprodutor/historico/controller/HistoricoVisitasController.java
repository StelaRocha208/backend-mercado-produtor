package br.com.mercadoprodutor.historico.controller;

import br.com.mercadoprodutor.historico.dto.VisitHistoryExportDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryFilterDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryPageResponseDTO;
import br.com.mercadoprodutor.historico.service.HistoricoVisitasService;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import br.com.mercadoprodutor.historico.service.PdfExportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historico-visitas")
@RequiredArgsConstructor
public class HistoricoVisitasController {

    private final HistoricoVisitasService service;
    private final PdfExportService pdfExportService;

    @GetMapping
    public ResponseEntity<VisitHistoryPageResponseDTO> listar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String statusPagamento,
            @RequestParam(required = false) String nomeProdutor,
            @RequestParam(required = false) String cpfProdutor,
            @PageableDefault(size = 20, sort = "dataEntrada", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        VisitHistoryFilterDTO filter = new VisitHistoryFilterDTO(
                dataInicio,
                dataFim,
                secao,
                statusPagamento,
                nomeProdutor,
                cpfProdutor
        );

        return ResponseEntity.ok(service.listarHistorico(filter, usuario, pageable));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportarCsv(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String statusPagamento,
            @RequestParam(required = false) String nomeProdutor,
            @RequestParam(required = false) String cpfProdutor
    ) {
        VisitHistoryFilterDTO filter = new VisitHistoryFilterDTO(
                dataInicio,
                dataFim,
                secao,
                statusPagamento,
                nomeProdutor,
                cpfProdutor
        );

        List<VisitHistoryExportDTO> dados = service.listarParaExportacao(filter, usuario);
        StringBuilder csv = new StringBuilder();
        csv.append("id;produtorNome;produtorCpf;dataVisita;secao;numeroEspaco;horarioEntrada;horarioSaida;tempoPermanencia;valorCobrado;statusPagamento\n");
        for (VisitHistoryExportDTO item : dados) {
            csv.append(item.id()).append(';')
                    .append(item.produtorNome()).append(';')
                    .append(item.produtorCpf()).append(';')
                    .append(item.dataVisita()).append(';')
                    .append(item.secao()).append(';')
                    .append(item.numeroEspaco()).append(';')
                    .append(item.horarioEntrada()).append(';')
                    .append(item.horarioSaida()).append(';')
                    .append(item.tempoPermanencia()).append(';')
                    .append(item.valorCobrado()).append(';')
                    .append(item.statusPagamento()).append('\n');
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historico-visitas.csv");
        return new ResponseEntity<>(csv.toString(), headers, HttpStatus.OK);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String secao,
            @RequestParam(required = false) String statusPagamento,
            @RequestParam(required = false) String nomeProdutor,
            @RequestParam(required = false) String cpfProdutor
    ) {
        VisitHistoryFilterDTO filter = new VisitHistoryFilterDTO(
                dataInicio,
                dataFim,
                secao,
                statusPagamento,
                nomeProdutor,
                cpfProdutor
        );

        List<VisitHistoryExportDTO> dados = service.listarParaExportacao(filter, usuario);
        byte[] pdfBytes = pdfExportService.gerarPdf(dados, service.resumoParaExportacao(filter, usuario));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historico-visitas.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
