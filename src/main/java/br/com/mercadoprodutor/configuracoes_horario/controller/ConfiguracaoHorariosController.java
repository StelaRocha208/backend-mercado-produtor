package br.com.mercadoprodutor.configuracoes_horario.controller;

import br.com.mercadoprodutor.configuracoes_horario.dto.ConfiguracaoHorariosDTO;
import br.com.mercadoprodutor.configuracoes_horario.service.ConfiguracaoHorariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracoes_horarios")
@RequiredArgsConstructor
public class ConfiguracaoHorariosController {

    private final ConfiguracaoHorariosService service;

    @GetMapping
    public ResponseEntity<ConfiguracaoHorariosDTO> getConfiguracoes() {
        return ResponseEntity.ok(service.getConfiguracoes());

    }

    @PutMapping
    public ResponseEntity<ConfiguracaoHorariosDTO> atualizarConfiguracoes(@RequestBody ConfiguracaoHorariosDTO novosDados) {
        return ResponseEntity.ok(service.atualizarConfiguracoes(novosDados));

    }

}