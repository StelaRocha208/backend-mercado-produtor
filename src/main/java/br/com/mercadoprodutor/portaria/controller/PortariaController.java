package br.com.mercadoprodutor.portaria.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
import br.com.mercadoprodutor.portaria.service.IRegistroService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/portaria")
@RequiredArgsConstructor
public class PortariaController {

    private final IRegistroService registroService;

    @PostMapping("/entrada")
    public ResponseEntity<RegistroResponseDTO> registrarEntrada(
            @RequestBody RegistroEntradaDTO dto) {

        RegistroResponseDTO registro =
                registroService.registrarEntrada(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registro);
    }
}