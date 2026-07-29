package br.com.mercadoprodutor.relatorios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutorInadimplenteDTO(

        String id,

        String nome,

        String cpf,

        String telefone,

        BigDecimal valorEmAberto,

        LocalDate dataUltimaVisitaEmAberto

) {}