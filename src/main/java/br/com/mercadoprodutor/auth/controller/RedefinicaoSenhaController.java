package br.com.mercadoprodutor.auth.controller;

import br.com.mercadoprodutor.auth.dto.EsqueciSenhaDTO;
import br.com.mercadoprodutor.auth.dto.RedefinirSenhaDTO;
import br.com.mercadoprodutor.auth.service.RedefinicaoSenhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RedefinicaoSenhaController {

    private final RedefinicaoSenhaService redefinicaoSenhaService;

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> forgotPassword(@RequestBody EsqueciSenhaDTO dto) {
        redefinicaoSenhaService.solicitarRecuperacao(dto.login());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> resetPassword(@RequestBody RedefinirSenhaDTO dto) {
        redefinicaoSenhaService.redefinirSenha(dto.token(), dto.senhaNova());
        return ResponseEntity.ok().build();
    }
}
