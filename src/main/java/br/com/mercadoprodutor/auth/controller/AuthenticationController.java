package br.com.mercadoprodutor.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import br.com.mercadoprodutor.auth.dto.AuthenticationDTO;
import br.com.mercadoprodutor.auth.dto.LoginResponseDTO;
import br.com.mercadoprodutor.auth.service.TokenService;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {

        var senhaUsuario = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(senhaUsuario);

        Usuario usuarioLogado = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(usuarioLogado);

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                usuarioLogado.getNome(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfis().name()
        );

        return ResponseEntity.ok(response);
    }
}
