package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.audit.aspect.AuditarAcao;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.reservas.dto.CriarReservaAdministrativaRequest;
import br.com.mercadoprodutor.reservas.dto.CriarSubstituicaoRequest;
import br.com.mercadoprodutor.reservas.dto.EncerrarReservaRequest;
import br.com.mercadoprodutor.reservas.dto.ReservaResponse;
import br.com.mercadoprodutor.reservas.mapper.ReservaMapper;
import br.com.mercadoprodutor.reservas.model.Reserva;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ProdutorReservaRepository;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Gerencia reservas registradas pelo administrador e ocupações fixas. */
@Service
@RequiredArgsConstructor
public class ReservaAdministrativaService {

    private final ReservaRepository reservaRepository;
    private final ProdutorReservaRepository produtorReservaRepository;
    private final ReservaMapper reservaMapper;
    private final PoliticaPeriodoReserva politicaPeriodoReserva;
    private final ValidadorDisponibilidadeReserva validadorDisponibilidade;

    @AuditarAcao(valor = "Reserva Administrativa Criada")
    @Transactional
    public ReservaResponse criar(CriarReservaAdministrativaRequest request) {
        LocalDate hoje = LocalDate.now();
        validarDataInicial(request.dataInicio(), hoje);

        Produtor produtor = buscarProdutor(request.usuarioId());
        Espaco espaco = validadorDisponibilidade.buscarEspacoReservavel(
                request.espacoId()
        );

        LocalDate dataFim = politicaPeriodoReserva.calcularDataFim(
                request.tipoReserva(),
                espaco.getSecao().getTipo(),
                request.dataInicio(),
                request.dataFim()
        );
        validadorDisponibilidade.validarAusenciaDeConflito(
                espaco,
                request.dataInicio(),
                dataFim
        );

        Reserva reserva = criarReserva(
                request,
                produtor,
                espaco,
                dataFim
        );

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @AuditarAcao(valor = "Reserva Encerrada pela Administração")
    @Transactional
    public ReservaResponse encerrar(
            String reservaId,
            EncerrarReservaRequest request
    ) {
        Reserva reserva = buscarReservaParaAtualizacao(reservaId);

        if (reserva.getStatusReserva().finalizado()
                || reserva.getDataEncerramento() != null) {
            throw conflito("A reserva já foi finalizada.");
        }

        LocalDate hoje = LocalDate.now();
        if (request.dataEncerramento().isAfter(hoje)) {
            throw requisicaoInvalida(
                    "A data de encerramento não pode estar no futuro."
            );
        }

        if (request.dataEncerramento().isBefore(reserva.getDataInicio())) {
            reserva.cancelar(request.motivo());
            return reservaMapper.toResponse(reservaRepository.save(reserva));
        }

        if (request.dataEncerramento().isAfter(reserva.getDataFim())) {
            throw requisicaoInvalida(
                    "A data de encerramento deve estar dentro do período da reserva."
            );
        }

        reserva.encerrar(request.dataEncerramento(), request.motivo());
        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @AuditarAcao(valor = "Substituição Provisória de Reserva Criada")
    @Transactional
    public ReservaResponse criarSubstituicao(
            String reservaEncerradaId,
            CriarSubstituicaoRequest request
    ) {
        Reserva reservaEncerrada = buscarReservaParaAtualizacao(
                reservaEncerradaId
        );
        validarOcupacaoFixa(reservaEncerrada);

        if (reservaEncerrada.getStatusReserva() != StatusReserva.ENCERRADA
                || reservaEncerrada.getDataEncerramento() == null) {
            throw conflito(
                    "Encerre a reserva atual antes de cadastrar um produtor provisório."
            );
        }

        Produtor substituto = buscarProdutor(request.usuarioId());
        if (Objects.equals(
                substituto.getId(),
                reservaEncerrada.getProdutor().getId()
        )) {
            throw requisicaoInvalida(
                    "O produtor provisório deve ser diferente do produtor que saiu."
            );
        }

        LocalDate primeiraDataDisponivel = reservaEncerrada
                .getDataEncerramento()
                .plusDays(1);
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = request.dataInicio() == null
                ? dataMaisRecente(primeiraDataDisponivel, hoje)
                : request.dataInicio();

        validarDataInicial(dataInicio, hoje);

        if (dataInicio.isBefore(primeiraDataDisponivel)) {
            throw requisicaoInvalida(
                    "A substituição deve começar após o encerramento da reserva anterior."
            );
        }

        if (dataInicio.isAfter(reservaEncerrada.getDataFim())) {
            throw requisicaoInvalida(
                    "Não há período restante para uma substituição provisória."
            );
        }

        Espaco espaco = validadorDisponibilidade.buscarEspacoReservavel(
                reservaEncerrada.getEspaco().getId()
        );
        validadorDisponibilidade.validarAusenciaDeConflito(
                espaco,
                dataInicio,
                reservaEncerrada.getDataFim()
        );

        Reserva reservaOrigem = reservaEncerrada.getReservaOrigem() == null
                ? reservaEncerrada
                : reservaEncerrada.getReservaOrigem();

        Reserva substituicao = Reserva.criarProvisoria(
                substituto,
                espaco,
                reservaOrigem,
                dataInicio,
                reservaEncerrada.getDataFim(),
                request.observacao()
        );

        return reservaMapper.toResponse(reservaRepository.save(substituicao));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAllComDetalhes()
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(String reservaId) {
        Reserva reserva = reservaRepository.findByIdComDetalhes(reservaId)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada."));
        return reservaMapper.toResponse(reserva);
    }

    private Reserva criarReserva(
            CriarReservaAdministrativaRequest request,
            Produtor produtor,
            Espaco espaco,
            LocalDate dataFim
    ) {
        return switch (request.tipoReserva()) {
            case DIARIA -> {
                Reserva reserva = Reserva.criarDiaria(
                        produtor,
                        espaco,
                        request.dataInicio(),
                        dataFim,
                        request.observacao()
                );
                reserva.confirmar();
                yield reserva;
            }
            case PRIORITARIA -> Reserva.criarPrioritaria(
                    produtor,
                    espaco,
                    request.dataInicio(),
                    dataFim,
                    request.observacao()
            );
            case TITULAR -> Reserva.criarTitular(
                    produtor,
                    espaco,
                    request.dataInicio(),
                    dataFim,
                    request.observacao()
            );
            case PROVISORIA -> throw requisicaoInvalida(
                    "A reserva provisória deve ser criada como substituição."
            );
        };
    }

    private Produtor buscarProdutor(String usuarioId) {
        return produtorReservaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> naoEncontrado(
                        "Produtor não encontrado para o usuário informado."
                ));
    }

    private Reserva buscarReservaParaAtualizacao(String reservaId) {
        return reservaRepository.findByIdParaAtualizacao(reservaId)
                .orElseThrow(() -> naoEncontrado("Reserva não encontrada."));
    }

    private void validarDataInicial(LocalDate dataInicio, LocalDate hoje) {
        if (dataInicio == null) {
            throw requisicaoInvalida("A data inicial é obrigatória.");
        }
        if (dataInicio.isBefore(hoje)) {
            throw requisicaoInvalida("A data inicial não pode estar no passado.");
        }
    }

    private void validarOcupacaoFixa(Reserva reserva) {
        if (!TipoReserva.ocupacoesFixas().contains(reserva.getTipoReserva())) {
            throw requisicaoInvalida(
                    "A substituição provisória exige uma ocupação fixa."
            );
        }
    }

    private LocalDate dataMaisRecente(LocalDate primeira, LocalDate segunda) {
        return primeira.isAfter(segunda) ? primeira : segunda;
    }

    private ResponseStatusException requisicaoInvalida(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

    private ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }

    private ResponseStatusException naoEncontrado(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }
}
