package br.com.mercadoprodutor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.mercadoprodutor.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(
            @RequestBody UsuarioCreateDTO dto) {

        UsuarioResponseDTO usuario = service.criarUsuario(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {

        return ResponseEntity.ok(
                service.listarUsuarios()
        );
    }
}