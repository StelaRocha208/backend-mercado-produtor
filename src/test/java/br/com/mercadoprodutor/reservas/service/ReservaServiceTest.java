package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.Secao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.service.EspacoService;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.repository.ProdutorReservaRepository;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ProdutorReservaRepository produtorReservaRepository;
    @Mock
    private EspacoService espacoService;
    @Mock
    private ReservaMapper reservaMapper;
    @Mock
    private ValidadorDisponibilidadeReserva validadorDisponibilidade;
    @Mock
    private Produtor produtor;
    @Mock
    private Espaco espaco;
    @Mock
    private Secao secao;

    private ReservaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaService(
                reservaRepository,
                produtorReservaRepository,
                espacoService,
                reservaMapper,
                validadorDisponibilidade
        );
    }

    @Test
    void produtorSoPodeCriarReservaDiariaEmEspacoVolatil() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        when(produtorReservaRepository.findByUsuarioId("usuario-1"))
                .thenReturn(Optional.of(produtor));
        when(validadorDisponibilidade.buscarEspacoReservavel("espaco-1"))
                .thenReturn(espaco);
        when(espaco.getSecao()).thenReturn(secao);
        when(secao.getTipo()).thenReturn(TipoSecao.VOLATIL);

        assertThrows(ResponseStatusException.class, () -> service.criar(
                new CriarReservaRequest(
                        "espaco-1",
                        inicio,
                        inicio.plusDays(1),
                        null
                ),
                "usuario-1"
        ));
    }
}
