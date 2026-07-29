package br.com.mercadoprodutor.dashboard.controller;

import br.com.mercadoprodutor.dashboard.service.DashboardService;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new DashboardController(dashboardService);
    }

    @Test
    void devePermitirAcessoAoAdministrador() {
        Usuario administrador = new Usuario(
                "Administrador",
                "admin@mercado.ba.gov.br",
                "senha",
                PerfilUsuario.ADMINISTRADOR
        );

        controller.obterIndicadores(administrador);

        verify(dashboardService).obterIndicadores();
    }

    @Test
    void deveBloquearAcessoDeOutrosPerfis() {
        Usuario operador = new Usuario(
                "Operador",
                "operador@mercado.ba.gov.br",
                "senha",
                PerfilUsuario.OPERADOR
        );

        assertThatThrownBy(() -> controller.obterIndicadores(operador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }
}
