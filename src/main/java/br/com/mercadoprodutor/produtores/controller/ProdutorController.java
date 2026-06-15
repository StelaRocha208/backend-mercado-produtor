package br.com.mercadoprodutor.produtores.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.mercadoprodutor.produtores.service.IProdutorService;
import br.com.mercadoprodutor.produtores.dto.ProdutorCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtores")
@RequiredArgsConstructor
public class ProdutorController {

    private final IProdutorService service;

    @PostMapping
    public ResponseEntity<Void> criarProdutor(@RequestBody @Valid ProdutorCreateDTO dto) {
        service.criarProdutor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
