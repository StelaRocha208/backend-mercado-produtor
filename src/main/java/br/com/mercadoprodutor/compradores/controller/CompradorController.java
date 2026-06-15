package br.com.mercadoprodutor.compradores.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mercadoprodutor.compradores.dto.CompradorCreateDTO;
import br.com.mercadoprodutor.compradores.service.ICompradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compradores")
@RequiredArgsConstructor
public class CompradorController {

    private final ICompradorService service;

    @PostMapping
    public ResponseEntity<Void> criarComprador(@RequestBody @Valid CompradorCreateDTO dto) {
        service.criarComprador(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
