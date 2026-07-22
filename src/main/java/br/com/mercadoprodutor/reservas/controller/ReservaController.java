package br.com.mercadoprodutor.reservas.controller;

import br.com.mercadoprodutor.core.exception.ErroPadraoDTO;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.CriarReservaAdministrativaRequest;
import br.com.mercadoprodutor.reservas.dto.CriarSubstituicaoRequest;
import br.com.mercadoprodutor.reservas.dto.EncerrarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.service.ReservaAdministrativaService;
import br.com.mercadoprodutor.reservas.service.ReservaService;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Expõe as operações de reservas diárias e administrativas.
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaAdministrativaService reservaAdministrativaService;

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

    @PostMapping("/administrativas")
    public ResponseEntity<ReservaResponse> criarAdministrativa(
            @RequestBody @Valid CriarReservaAdministrativaRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservaAdministrativaService.criar(request));
    }

    @PostMapping("/administrativas/{reservaId}/encerramento")
    public ResponseEntity<ReservaResponse> encerrarAdministrativa(
            @PathVariable String reservaId,
            @RequestBody @Valid EncerrarReservaRequest request
    ) {
        return ResponseEntity.ok(
                reservaAdministrativaService.encerrar(reservaId, request)
        );
    }

    @PostMapping("/administrativas/{reservaId}/substituicoes")
    public ResponseEntity<ReservaResponse> criarSubstituicao(
            @PathVariable String reservaId,
            @RequestBody @Valid CriarSubstituicaoRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservaAdministrativaService.criarSubstituicao(
                        reservaId,
                        request
                ));
    }

    @GetMapping("/administrativas")
    public ResponseEntity<List<ReservaResponse>> listarParaAdministracao() {
        return ResponseEntity.ok(reservaAdministrativaService.listarTodas());
    }

    @GetMapping("/administrativas/{reservaId}")
    public ResponseEntity<ReservaResponse> buscarParaAdministracaoPorId(
            @PathVariable String reservaId
    ) {
        return ResponseEntity.ok(
                reservaAdministrativaService.buscarPorId(reservaId)
        );
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
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim
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
