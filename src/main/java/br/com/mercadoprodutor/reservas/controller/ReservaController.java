package br.com.mercadoprodutor.reservas.controller;

import br.com.mercadoprodutor.core.exception.ErroPadraoDTO;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.service.ReservaService;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid CriarReservaRequest request
    ) {
        ReservaResponse response = reservaService.criar(
                request,
                obterUsuarioId(usuario)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ReservaResponse>> listarMinhas(
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(
                reservaService.listarMinhas(obterUsuarioId(usuario))
        );
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<EspacoResponse>> listarDisponibilidade(
            @RequestParam(required = false) TipoSecao secao,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(
                reservaService.listarDisponibilidade(secao, dataInicio, dataFim)
        );
    }

    @GetMapping("/produtor/{produtorId}")
    public ResponseEntity<List<ReservaResponse>> listarPorProdutor(
            @PathVariable String produtorId
    ) {
        return ResponseEntity.ok(reservaService.listarPorProdutor(produtorId));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroPadraoDTO> tratarErroReserva(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        ErroPadraoDTO erro = new ErroPadraoDTO(
                LocalDateTime.now(),
                exception.getStatusCode().value(),
                "Erro no módulo de reservas",
                exception.getReason(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(exception.getStatusCode()).body(erro);
    }

    private String obterUsuarioId(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado para acessar as reservas."
            );
        }

        return usuario.getId();
    }
}
