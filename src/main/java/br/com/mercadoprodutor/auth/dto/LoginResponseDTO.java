package br.com.mercadoprodutor.auth.dto;

public record LoginResponseDTO(
        String token,
        String nome,
        String email,
        String perfil
) {}
