package br.com.mercadoprodutor.relatorios.dto;

import java.math.BigDecimal;

public record ResumoInadimplenciaDTO(

        BigDecimal totalGeralEmAberto,

        Long quantidadeProdutoresInadimplentes,

        BigDecimal valorMedioEmAberto

) {}