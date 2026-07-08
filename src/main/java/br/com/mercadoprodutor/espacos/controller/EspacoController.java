package br.com.mercadoprodutor.espacos.controller;

import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.dto.OcupacaoResumoResponse;
import br.com.mercadoprodutor.espacos.dto.SecaoResponse;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.service.EspacoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EspacoController {

    private final EspacoService espacoService;

    @GetMapping("/secoes")
    public ResponseEntity<List<SecaoResponse>> listarSecoes() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/espacos")
    public ResponseEntity<List<EspacoResponse>> listarEspacos(
        @RequestParam(required = false) TipoSecao secao
    ) {
        return ResponseEntity.ok(espacoService.listarEspacos(secao, null, null));
    }

    @GetMapping("/ocupacao/mapa")
    public ResponseEntity<List<EspacoResponse>> obterMapaOcupacao(
        @RequestParam(required = false) TipoSecao secao
    ) {
        return ResponseEntity.ok(espacoService.listarEspacos(secao, null, null));
    }


}