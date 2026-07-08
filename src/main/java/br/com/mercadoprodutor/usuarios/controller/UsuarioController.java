package br.com.mercadoprodutor.usuarios.controller;

import br.com.mercadoprodutor.usuarios.dto.UsuarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.mercadoprodutor.usuarios.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.usuarios.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.usuarios.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody @Valid UsuarioCreateDTO dto) {
        UsuarioResponseDTO usuario = service.criarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable String id,
            @RequestBody @Valid UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(service.editarUsuario(id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(service.listarUsuarios(busca, pageable));
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<Object> buscarDetalhes(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                service.buscarDetalhesUsuario(id)
        );
    }
}
