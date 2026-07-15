package br.com.mercadoprodutor.auth.dto;

import java.util.List;

public record LoginResponseDTO(
        String token,
        String nome,
        String email,
        String perfilAtivo,
        List<String> perfis
) {}
