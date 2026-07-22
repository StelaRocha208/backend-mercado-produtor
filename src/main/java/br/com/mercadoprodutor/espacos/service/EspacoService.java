package br.com.mercadoprodutor.espacos.service;

import br.com.mercadoprodutor.audit.aspect.AuditarAcao;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.espacos.dto.AtualizarAreaSecaoRequest;
import br.com.mercadoprodutor.espacos.dto.AtualizarTarifaSecaoRequest;
import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.dto.SecaoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.espacos.repository.SecaoRepository;
import br.com.mercadoprodutor.reservas.model.StatusReserva;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import br.com.mercadoprodutor.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * Monta o mapa considerando reservas diárias e ocupações administrativas.
 */
@Service
@RequiredArgsConstructor
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final SecaoRepository secaoRepository;
    private final ReservaRepository reservaRepository;

    @Transactional(readOnly = true)
    public List<SecaoResponse> listarSecoes() {
        return secaoRepository.findByAtivaTrueOrderByOrdemVisualAsc()
                .stream()
                .map(SecaoResponse::fromEntity)
                .toList();
    }

    @AuditarAcao(valor = "Tarifa por metro quadrado atualizada")
    @Transactional
    public SecaoResponse atualizarTarifa(
            String secaoId,
            AtualizarTarifaSecaoRequest request
    ) {
        var secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new RegraNegocioException(
                        "Seção não encontrada."
                ));

        secao.setTaxaPorM2(request.taxaPorM2());
        return SecaoResponse.fromEntity(secao);
    }

    @AuditarAcao(valor = "Área das vagas da seção atualizada")
    @Transactional
    public void atualizarArea(
            String secaoId,
            AtualizarAreaSecaoRequest request
    ) {
        if (!secaoRepository.existsById(secaoId)) {
            throw new RegraNegocioException("Seção não encontrada.");
        }

        espacoRepository.atualizarAreaM2PorSecao(
                secaoId,
                request.areaM2()
        );
    }

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
                espaco.getSecao().getTaxaPorM2(),
                calcularValorDiaria(espaco),
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

    private BigDecimal calcularValorDiaria(Espaco espaco) {
        BigDecimal areaM2 = espaco.getAreaM2();
        BigDecimal tarifaPorM2 = espaco.getSecao().getTaxaPorM2();

        if (areaM2 == null || tarifaPorM2 == null) {
            return null;
        }

        return areaM2.multiply(tarifaPorM2)
                .setScale(2, RoundingMode.HALF_UP);
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
