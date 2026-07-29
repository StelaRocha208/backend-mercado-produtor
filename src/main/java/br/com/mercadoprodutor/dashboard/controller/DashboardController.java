package br.com.mercadoprodutor.dashboard.controller;

import br.com.mercadoprodutor.dashboard.dto.DashboardResponse;
import br.com.mercadoprodutor.dashboard.service.DashboardService;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/indicadores")
    public ResponseEntity<DashboardResponse> obterIndicadores(
            @AuthenticationPrincipal Usuario usuario
    ) {
        if (usuario == null
                || usuario.getPerfis() != PerfilUsuario.ADMINISTRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso permitido apenas para administradores."
            );
        }

        return ResponseEntity.ok(dashboardService.obterIndicadores());
    }
}
