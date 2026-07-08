package br.com.mercadoprodutor.reservas.controller;

import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(
            @RequestBody @Valid CriarReservaRequest request
    ) {
        ReservaResponse response = reservaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/produtor/{produtorId}")
    public ResponseEntity<?> listarPorProdutor(
            @PathVariable String produtorId
    ) {
        return ResponseEntity.ok(reservaService.listarPorProdutor(produtorId));
    }
}