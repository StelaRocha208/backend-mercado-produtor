package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.espacos.service.EspacoService;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.repository.ProdutorReservaRepository;
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
    private final ProdutorReservaRepository produtorReservaRepository;
    private final EspacoService espacoService;
    private final ReservaMapper reservaMapper;

    @Transactional
    public ReservaResponse criar(CriarReservaRequest request, String usuarioId) {
        validarPeriodo(request.dataInicio(), request.dataFim());

        Produtor produtor = buscarProdutorPorUsuario(usuarioId);
        Espaco espaco = buscarEspaco(request.espacoId());

        validarEspacoReservavel(espaco);
        validarSecaoReservavel(espaco);
        validarConflitoDeReserva(espaco, request.dataInicio(), request.dataFim());

        Reserva reserva = Reserva.criar(
                produtor,
                espaco,
                request.dataInicio(),
                request.dataFim(),
                request.observacao()
        );

        Reserva reservaSalva = reservaRepository.save(reserva);

        return reservaMapper.toResponse(reservaSalva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarMinhas(String usuarioId) {
        Produtor produtor = buscarProdutorPorUsuario(usuarioId);

        return listarPorProdutor(produtor.getId());
    }

    @Transactional(readOnly = true)
    public List<EspacoResponse> listarDisponibilidade(
            TipoSecao tipoSecao,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        validarPeriodo(dataInicio, dataFim);

        return espacoService.obterMapaOcupacao(tipoSecao, dataInicio, dataFim);
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

    private Produtor buscarProdutorPorUsuario(String usuarioId) {
        return produtorReservaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RegraNegocioException(
                        "Produtor não encontrado para o usuário autenticado."
                ));
    }

    private Espaco buscarEspaco(String espacoId) {
        return espacoRepository.findById(espacoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Espaço não encontrado."
                ));
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

        if (dataInicio.isBefore(LocalDate.now()) || dataFim.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "As datas da reserva não podem estar no passado."
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

        if (espaco.getStatusOcupacao() != StatusOcupacao.LIVRE) {
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
                StatusReserva.bloqueantes()
        );

        if (existeConflito) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O espaço já possui reserva para o período informado."
            );
        }
    }
}
