package br.com.mercadoprodutor.auth.controller;

import br.com.mercadoprodutor.usuarios.service.IUsuarioService;
import br.com.mercadoprodutor.auth.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.com.mercadoprodutor.auth.dto.AuthenticationDTO;
import br.com.mercadoprodutor.auth.dto.LoginResponseDTO;
import br.com.mercadoprodutor.auth.service.TokenService;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final IUsuarioService usuarioService;

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {

        var senhaUsuario = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(senhaUsuario);

        Usuario usuarioLogado = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(usuarioLogado);

        List<String> listaDePerfis = usuarioLogado.getPerfis().stream().map(Enum::name).toList();

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                usuarioLogado.getNome(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfilAtivo(), // Retorna o ativo
                listaDePerfis
        );

        return ResponseEntity.ok(response);
    }

    //rota nva pra trocar perfil
    @PostMapping("/trocar-perfil")
    public ResponseEntity<LoginResponseDTO> trocarPerfil(
            @RequestBody @Valid br.com.mercadoprodutor.auth.dto.TrocarPerfilDTO dto, // Crie esse DTO com uma String novoPerfil
            org.springframework.security.core.Authentication authentication) {

        String email = authentication.getName();
        String novoToken = authenticationService.alternarPerfilAtivo(email, dto.novoPerfil());

        Usuario usuarioAtualizado = (Usuario) authenticationService.loadUserByUsername(email);
        List<String> listaDePerfis = usuarioAtualizado.getPerfis().stream().map(Enum::name).toList();

        LoginResponseDTO response = new LoginResponseDTO(
                novoToken,
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getPerfilAtivo(),
                listaDePerfis
        );

        return ResponseEntity.ok(response);
    }


}
