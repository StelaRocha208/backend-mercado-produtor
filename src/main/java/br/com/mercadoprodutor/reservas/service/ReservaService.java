package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.service.EspacoService;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.reservas.dto.CriarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.repository.ProdutorReservaRepository;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** Gerencia reservas diárias e consultas comuns do produtor. */
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProdutorReservaRepository produtorReservaRepository;
    private final EspacoService espacoService;
    private final ReservaMapper reservaMapper;
    private final ValidadorDisponibilidadeReserva validadorDisponibilidade;

    @Transactional
    public ReservaResponse criar(CriarReservaRequest request, String usuarioId) {
        validarPeriodo(request.dataInicio(), request.dataFim());

        Produtor produtor = buscarProdutorPorUsuario(usuarioId);
        Espaco espaco = validadorDisponibilidade.buscarEspacoReservavel(
                request.espacoId()
        );

        validarSecaoReservavelPeloProdutor(espaco);
        validarReservaDiaria(request.dataInicio(), request.dataFim());
        validadorDisponibilidade.validarAusenciaDeConflito(
                espaco,
                request.dataInicio(),
                request.dataFim()
        );

        Reserva reserva = Reserva.criarDiaria(
                produtor,
                espaco,
                request.dataInicio(),
                request.dataFim(),
                request.observacao()
        );

        return reservaMapper.toResponse(reservaRepository.save(reserva));
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
            throw requisicaoInvalida("O produtor é obrigatório.");
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

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw requisicaoInvalida(
                    "Data inicial e data final são obrigatórias."
            );
        }

        if (dataFim.isBefore(dataInicio)) {
            throw requisicaoInvalida(
                    "A data final não pode ser anterior à data inicial."
            );
        }

        LocalDate hoje = LocalDate.now();
        if (dataInicio.isBefore(hoje) || dataFim.isBefore(hoje)) {
            throw requisicaoInvalida(
                    "As datas da reserva não podem estar no passado."
            );
        }
    }

    private void validarSecaoReservavelPeloProdutor(Espaco espaco) {
        if (espaco.getSecao().getTipo() != TipoSecao.VOLATIL) {
            throw requisicaoInvalida(
                    "A reserva diária está disponível apenas para espaços voláteis."
            );
        }
    }

    private void validarReservaDiaria(LocalDate dataInicio, LocalDate dataFim) {
        if (!dataInicio.equals(dataFim)) {
            throw requisicaoInvalida(
                    "O produtor pode reservar um espaço volátil por apenas um dia."
            );
        }
    }

    private ResponseStatusException requisicaoInvalida(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
