package br.com.mercadoprodutor.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata a nossa exceção de Regra de Negócio (ex: E-mail duplicado)
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroPadraoDTO> handleRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        ErroPadraoDTO erro = new ErroPadraoDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Violação de Regra de Negócio",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // Trata erros das anotações @Valid (ex: senha em branco, email inválido)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Pega todos os campos que deram erro e formata uma lista
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());

        ErroPadraoDTO erro = new ErroPadraoDTO(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(), // Status 422 (Entidade Não Processável)
                "Erro de Validação",
                "Um ou mais campos estão inválidos",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    // Pega qualquer outro erro desconhecido (Erro 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadraoDTO> handleExceptionGenerica(Exception ex, HttpServletRequest request) {

        // Aqui no mundo real você colocaria um log.error(ex.getMessage(), ex); para ver no console

        ErroPadraoDTO erro = new ErroPadraoDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no sistema. Contate o administrador.",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
