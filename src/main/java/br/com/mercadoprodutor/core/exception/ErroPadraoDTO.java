package br.com.mercadoprodutor.core.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroPadraoDTO(
        LocalDateTime timestamp,
        Integer status,
        String erro,
        String mensagem,
        String path,
        List<String> detalhes
) {}
