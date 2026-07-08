package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EspacoRepository espacoRepository;
    private final ProdutorRepository produtorRepository;
    private final ReservaMapper reservaMapper;

    @Transactional
    public ReservaResponse criar(CriarReservaRequest request) {
        validarPeriodo(request.dataInicio(), request.dataFim());

        Produtor produtor = produtorRepository.findById(request.produtorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produtor não encontrado."
                ));

        Espaco espaco = espacoRepository.findById(request.espacoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Espaço não encontrado."
                ));

        validarEspacoReservavel(espaco);
        validarSecaoReservavel(espaco);
        validarConflitoDeReserva(espaco, request.dataInicio(), request.dataFim());

        Reserva reserva = Reserva.builder()
                .produtor(produtor)
                .espaco(espaco)
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .statusReserva(StatusReserva.AGUARDANDO_CONFIRMACAO)
                .observacao(request.observacao())
                .build();

        Reserva reservaSalva = reservaRepository.save(reserva);

        return reservaMapper.toResponse(reservaSalva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorProdutor(String produtorId) {
        if (produtorId == null || produtorId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O produtor é obrigatório."
            );
        }

        return reservaRepository.findByProdutorIdComDetalhes(produtorId)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data inicial e data final são obrigatórias."
            );
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }

    private void validarEspacoReservavel(Espaco espaco) {
        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O espaço está inativo."
            );
        }

        if (espaco.getStatusOcupacao() == StatusOcupacao.OCUPADO ||
                espaco.getStatusOcupacao() == StatusOcupacao.INDISPONIVEL) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O espaço não está disponível para reserva."
            );
        }
    }

    private void validarSecaoReservavel(Espaco espaco) {
        TipoSecao tipoSecao = espaco.getSecao().getTipo();

        if (tipoSecao != TipoSecao.PEDRA && tipoSecao != TipoSecao.VOLANTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A reserva antecipada está disponível apenas para Pedra e Volante."
            );
        }
    }

    private void validarConflitoDeReserva(
            Espaco espaco,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        boolean existeConflito = reservaRepository.existsConflitoDePeriodo(
                espaco.getId(),
                dataInicio,
                dataFim,
                List.of(
                        StatusReserva.AGUARDANDO_CONFIRMACAO,
                        StatusReserva.CONFIRMADA
                )
        );

        if (existeConflito) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O espaço já possui reserva para o período informado."
            );
        }
    }
}