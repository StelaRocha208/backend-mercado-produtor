package br.com.mercadoprodutor.historico.service;

import br.com.mercadoprodutor.historico.dto.VisitHistoryFilterDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryPageResponseDTO;
import br.com.mercadoprodutor.historico.repository.HistoricoVisitasRepository;
import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HistoricoVisitasServiceTest {

    @Mock
    private HistoricoVisitasRepository registroRepository;

    @Mock
    private ProdutorRepository produtorRepository;

    @InjectMocks
    private HistoricoVisitasService service;

    @Test
    void deveCalcularResumoComValoresAgregados() {
        Usuario usuario = new Usuario("Admin", "admin@email.com", "senha", PerfilUsuario.ADMINISTRADOR);
        definirId(usuario, "user-1");

        Produtor produtor = new Produtor("12345678901", "77999990000", usuario);
        definirId(produtor, "prod-1");

        Registro registro = new Registro();
        definirId(registro, "reg-1");
        registro.setProdutor(produtor);
        registro.setDataEntrada(LocalDateTime.of(2026, 7, 27, 10, 0));
        registro.setDataSaida(LocalDateTime.of(2026, 7, 27, 12, 0));
        registro.setStatusRegistro(StatusRegistro.ENCERRADO);
        registro.setSecao("Pedra");
        registro.setEspaco("10");
        registro.setStatusPagamento(br.com.mercadoprodutor.historico.model.StatusPagamento.PAGO);
        registro.setValorCobrado(BigDecimal.valueOf(40.00));

        given(registroRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(registro), PageRequest.of(0, 10), 1));
        given(registroRepository.findAll(any(Specification.class)))
                .willReturn(List.of(registro));

        VisitHistoryPageResponseDTO response = service.listarHistorico(
                new VisitHistoryFilterDTO(null, null, null, null, null, null),
                usuario,
                PageRequest.of(0, 10)
        );

        assertThat(response.summary().totalVisitas()).isEqualTo(1L);
        assertThat(response.summary().valorTotalPago()).isEqualByComparingTo("40.00");
        assertThat(response.summary().valorTotalPendente()).isEqualByComparingTo("0.00");
        assertThat(response.summary().tempoMedioPermanencia()).isEqualTo("02:00");
        assertThat(response.content()).hasSize(1);
    }

    private void definirId(Object target, String id) {
        try {
            Field field = target.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Falha ao definir id da entidade", exception);
        }
    }
}
