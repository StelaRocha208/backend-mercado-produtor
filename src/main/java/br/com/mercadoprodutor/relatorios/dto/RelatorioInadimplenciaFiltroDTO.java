package br.com.mercadoprodutor.relatorios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioInadimplenciaFiltroDTO(

        String nome,

        String cpf,

        BigDecimal valorMinimo,

        BigDecimal valorMaximo,

        LocalDate dataVisitaDe,

        LocalDate dataVisitaAte,

        Integer pagina,

        Integer tamanho

) {}