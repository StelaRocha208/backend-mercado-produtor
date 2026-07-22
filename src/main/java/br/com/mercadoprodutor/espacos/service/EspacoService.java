package br.com.mercadoprodutor.espacos.service;

import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Monta o mapa considerando reservas diárias e ocupações administrativas.
 */
@Service
@RequiredArgsConstructor
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<EspacoResponse> listarEspacos(
            TipoSecao tipoSecao,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        List<Espaco> espacos = tipoSecao == null
                ? espacoRepository.findAllAtivosOrdenados()
                : espacoRepository.findAtivosByTipoSecao(tipoSecao);

        LocalDate inicioConsulta = dataInicio == null
                ? LocalDate.now()
                : dataInicio;
        LocalDate fimConsulta = dataFim == null ? inicioConsulta : dataFim;

        Set<String> espacosReservados = buscarEspacosReservados(
                inicioConsulta,
                fimConsulta,
                TipoReserva.reservasVolateis()
        );
        Set<String> espacosOcupados = buscarEspacosReservados(
                inicioConsulta,
                fimConsulta,
                TipoReserva.ocupacoesFixas()
        );

        return espacos.stream()
                .map(espaco -> toResponse(
                        espaco,
                        espacosReservados,
                        espacosOcupados
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EspacoResponse> obterMapaOcupacao(
            TipoSecao tipoSecao,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        return listarEspacos(tipoSecao, dataInicio, dataFim);
    }

    private Set<String> buscarEspacosReservados(
            LocalDate dataInicio,
            LocalDate dataFim,
            Set<TipoReserva> tiposReserva
    ) {
        if (dataInicio == null || dataFim == null) {
            return Set.of();
        }

        if (dataFim.isBefore(dataInicio)) {
            return Set.of();
        }

        return reservaRepository.findEspacosReservadosNoPeriodo(
                dataInicio,
                dataFim,
                StatusReserva.bloqueantes(),
                tiposReserva
        );
    }

    private EspacoResponse toResponse(
            Espaco espaco,
            Set<String> espacosReservados,
            Set<String> espacosOcupados
    ) {
        StatusOcupacao statusCalculado = calcularStatusOcupacao(
                espaco,
                espacosReservados,
                espacosOcupados
        );

        boolean selecionavel = Boolean.TRUE.equals(espaco.getAtivo())
                && statusCalculado == StatusOcupacao.LIVRE
                && secaoPermiteReserva(espaco.getSecao().getTipo());
        boolean selecionavelAdministracao = Boolean.TRUE.equals(
                espaco.getAtivo()
        )
                && statusCalculado == StatusOcupacao.LIVRE;

        String motivoBloqueio = obterMotivoBloqueio(espaco, statusCalculado);
        String motivoBloqueioAdministracao = obterMotivoBloqueioAdministracao(
                espaco,
                statusCalculado
        );

        return new EspacoResponse(
                espaco.getId(),
                espaco.getNumero(),
                espaco.getSecao().getId(),
                espaco.getSecao().getNome(),
                espaco.getSecao().getTipo(),
                espaco.getAreaM2(),
                statusCalculado,
                espaco.getAtivo(),
                espaco.getPavilhao(),
                espaco.getGrupoVisual(),
                espaco.getLinha(),
                espaco.getColuna(),
                espaco.getOrdemVisual(),
                selecionavel,
                motivoBloqueio,
                selecionavelAdministracao,
                motivoBloqueioAdministracao
        );
    }

    private StatusOcupacao calcularStatusOcupacao(
            Espaco espaco,
            Set<String> espacosReservados,
            Set<String> espacosOcupados
    ) {
        if (espaco.getStatusOcupacao() != StatusOcupacao.LIVRE) {
            return espaco.getStatusOcupacao();
        }

        if (espacosOcupados.contains(espaco.getId())) {
            return StatusOcupacao.OCUPADO;
        }

        if (espacosReservados.contains(espaco.getId())) {
            return StatusOcupacao.RESERVADO;
        }

        return espaco.getStatusOcupacao();
    }

    private boolean secaoPermiteReserva(TipoSecao tipoSecao) {
        return tipoSecao == TipoSecao.VOLATIL;
    }

    private String obterMotivoBloqueio(
            Espaco espaco,
            StatusOcupacao statusCalculado
    ) {
        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            return "Espaço inativo";
        }

        if (!secaoPermiteReserva(espaco.getSecao().getTipo())) {
            return "A reserva diária está disponível apenas para espaços voláteis";
        }

        return switch (statusCalculado) {
            case LIVRE -> null;
            case RESERVADO -> "Espaço já reservado para o período informado";
            case OCUPADO -> "Espaço ocupado";
            case INDISPONIVEL -> "Espaço indisponível";
        };
    }

    private String obterMotivoBloqueioAdministracao(
            Espaco espaco,
            StatusOcupacao statusCalculado
    ) {
        if (!Boolean.TRUE.equals(espaco.getAtivo())) {
            return "Espaço inativo";
        }

        return switch (statusCalculado) {
            case LIVRE -> null;
            case RESERVADO -> "Espaço reservado para o período informado";
            case OCUPADO -> "Espaço ocupado no período informado";
            case INDISPONIVEL -> "Espaço indisponível";
        };
    }
}
