package br.com.mercadoprodutor.relatorios.controller;

import br.com.mercadoprodutor.relatorios.dto.ProdutorInadimplenteDTO;
import br.com.mercadoprodutor.relatorios.dto.RelatorioInadimplenciaFiltroDTO;
import br.com.mercadoprodutor.relatorios.dto.ResumoInadimplenciaDTO;
import br.com.mercadoprodutor.relatorios.service.RelatorioInadimplenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios/inadimplencia")
@RequiredArgsConstructor
public class RelatorioInadimplenciaController {

    private final RelatorioInadimplenciaService relatorioService;


    @GetMapping
    public ResponseEntity<List<ProdutorInadimplenteDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String valorMinimo,
            @RequestParam(required = false) String valorMaximo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVisitaDe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVisitaAte,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanho
    ) {

        RelatorioInadimplenciaFiltroDTO filtro =
                new RelatorioInadimplenciaFiltroDTO(
                        nome,
                        cpf,
                        converterBigDecimal(valorMinimo),
                        converterBigDecimal(valorMaximo),
                        dataVisitaDe,
                        dataVisitaAte,
                        pagina,
                        tamanho
                );


        return ResponseEntity.ok(
                relatorioService.gerarRelatorio(filtro)
        );
    }


    @GetMapping("/resumo")
    public ResponseEntity<ResumoInadimplenciaDTO> resumo() {

        return ResponseEntity.ok(
                relatorioService.gerarResumo()
        );
    }


    private java.math.BigDecimal converterBigDecimal(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return new java.math.BigDecimal(valor);
    }
}