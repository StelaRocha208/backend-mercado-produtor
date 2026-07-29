package br.com.mercadoprodutor.relatorio.visitas.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioFiltroDTO;
import br.com.mercadoprodutor.relatorio.visitas.dto.RelatorioVisitaDTO;
import br.com.mercadoprodutor.relatorio.visitas.service.IRelatorioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorio/visitas")
@RequiredArgsConstructor
public class RelatorioController {

    private final IRelatorioService relatorioService;

    @GetMapping
    public List<RelatorioVisitaDTO> gerarRelatorio(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            @RequestParam(required = false)
            String secao,

            @RequestParam(required = false)
            String produtor


    ) {

        RelatorioFiltroDTO filtro = new RelatorioFiltroDTO(
                dataInicio,
                dataFim,
                secao,
                produtor
        );

        return relatorioService.gerarRelatorio(filtro);
    }
}