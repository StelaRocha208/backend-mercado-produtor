package br.com.mercadoprodutor.historico.service;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.core.exception.RecursoNaoEncontradoException;
import br.com.mercadoprodutor.historico.dto.VisitHistoryExportDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryFilterDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryPageResponseDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistoryResponseDTO;
import br.com.mercadoprodutor.historico.dto.VisitHistorySummaryDTO;
import br.com.mercadoprodutor.historico.model.StatusPagamento;
import br.com.mercadoprodutor.historico.repository.HistoricoVisitasRepository;
import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoVisitasService {

    private final HistoricoVisitasRepository repository;
    private final ProdutorRepository produtorRepository;

    public VisitHistoryPageResponseDTO listarHistorico(
            VisitHistoryFilterDTO filter,
            Usuario usuario,
            Pageable pageable
    ) {
        validarFiltro(filter);

        String produtorIdRestrito = resolverProdutorIdRestrito(usuario);
        Specification<Registro> spec = montarEspecificacoes(filter, produtorIdRestrito);

        Page<Registro> pageResult = repository.findAll(spec, pageable);
        List<Registro> registros = repository.findAll(spec);

        List<VisitHistoryResponseDTO> content = pageResult.getContent().stream()
                .map(this::mapearParaResponse)
                .toList();

        VisitHistorySummaryDTO summary = montarResumo(registros);

        return new VisitHistoryPageResponseDTO(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                summary
        );
    }

    public List<VisitHistoryExportDTO> listarParaExportacao(
            VisitHistoryFilterDTO filter,
            Usuario usuario
    ) {
        validarFiltro(filter);

        String produtorIdRestrito = resolverProdutorIdRestrito(usuario);
        Specification<Registro> spec = montarEspecificacoes(filter, produtorIdRestrito);

        return repository.findAll(spec).stream()
                .map(this::mapearParaExportacao)
                .toList();
    }

    public VisitHistorySummaryDTO resumoParaExportacao(
            VisitHistoryFilterDTO filter,
            Usuario usuario
    ) {
        validarFiltro(filter);

        String produtorIdRestrito = resolverProdutorIdRestrito(usuario);
        Specification<Registro> spec = montarEspecificacoes(filter, produtorIdRestrito);
        List<Registro> registros = repository.findAll(spec);

        return montarResumo(registros);
    }

    private String resolverProdutorIdRestrito(Usuario usuario) {
        if (usuario == null) {
            throw new RegraNegocioException("É necessário estar autenticado para consultar o histórico.");
        }

        if (usuario.getPerfis() == PerfilUsuario.ADMINISTRADOR || usuario.getPerfis() == PerfilUsuario.OPERADOR) {
            return null;
        }

        Produtor produtor = produtorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor não encontrado para o usuário autenticado."));

        return produtor.getId();
    }

    private Specification<Registro> montarEspecificacoes(
            VisitHistoryFilterDTO filter,
            String produtorIdRestrito
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.dataInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dataEntrada"),
                        filter.dataInicio().atStartOfDay()
                ));
            }

            if (filter.dataFim() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataEntrada"),
                        filter.dataFim().atTime(LocalTime.MAX)
                ));
            }

            if (StringUtils.hasText(filter.secao())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("secao")),
                        "%" + filter.secao().trim().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.statusPagamento())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("statusPagamento"),
                        StatusPagamento.valueOf(filter.statusPagamento().trim().toUpperCase())
                ));
            }

            if (StringUtils.hasText(filter.nomeProdutor())) {
                Join<Registro, Produtor> produtorJoin = root.join("produtor", JoinType.INNER);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(produtorJoin.get("usuario").get("nome")),
                        "%" + filter.nomeProdutor().trim().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.cpfProdutor())) {
                Join<Registro, Produtor> produtorJoin = root.join("produtor", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(
                        produtorJoin.get("cpf"),
                        filter.cpfProdutor().trim()
                ));
            }

            if (produtorIdRestrito != null) {
                predicates.add(criteriaBuilder.equal(root.get("produtor").get("id"), produtorIdRestrito));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private VisitHistoryResponseDTO mapearParaResponse(Registro registro) {
        return new VisitHistoryResponseDTO(
                registro.getId(),
                registro.getProdutor().getUsuario().getNome(),
                registro.getProdutor().getCpf(),
                registro.getDataEntrada() != null ? registro.getDataEntrada().toLocalDate() : null,
                registro.getSecao(),
                registro.getEspaco(),
                registro.getDataEntrada() != null ? registro.getDataEntrada().toLocalTime() : null,
                registro.getDataSaida() != null ? registro.getDataSaida().toLocalTime() : null,
                formatarTempoPermanencia(registro.getDataEntrada(), registro.getDataSaida()),
                resolverValorCobrado(registro),
                resolverStatusPagamento(registro)
        );
    }

    private VisitHistoryExportDTO mapearParaExportacao(Registro registro) {
        VisitHistoryResponseDTO response = mapearParaResponse(registro);
        return new VisitHistoryExportDTO(
                response.id(),
                response.produtorNome(),
                response.produtorCpf(),
                response.dataVisita(),
                response.secao(),
                response.numeroEspaco(),
                response.horarioEntrada(),
                response.horarioSaida(),
                response.tempoPermanencia(),
                response.valorCobrado(),
                response.statusPagamento()
        );
    }

    private VisitHistorySummaryDTO montarResumo(List<Registro> registros) {
        long totalVisitas = registros.size();
        BigDecimal valorTotalPago = registros.stream()
                .filter(registro -> isStatusPagamento(registro, StatusPagamento.PAGO))
                .map(this::resolverValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotalPendente = registros.stream()
                .filter(registro -> isStatusPagamento(registro, StatusPagamento.PENDENTE))
                .map(this::resolverValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long tempoTotalEmSegundos = registros.stream()
                .mapToLong(registro -> {
                    LocalDateTime entrada = registro.getDataEntrada();
                    LocalDateTime saida = registro.getDataSaida();
                    if (entrada == null || saida == null) {
                        return 0L;
                    }
                    return Duration.between(entrada, saida).getSeconds();
                })
                .sum();

        String tempoMedioPermanencia = totalVisitas == 0
                ? "00:00"
                : formatarDuracaoSegundos(tempoTotalEmSegundos / totalVisitas);

        return new VisitHistorySummaryDTO(
                totalVisitas,
                valorTotalPago,
                valorTotalPendente,
                tempoMedioPermanencia
        );
    }

    private void validarFiltro(VisitHistoryFilterDTO filter) {
        if (filter == null) {
            return;
        }

        if (filter.dataInicio() != null && filter.dataFim() != null && filter.dataInicio().isAfter(filter.dataFim())) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final.");
        }

        if (StringUtils.hasText(filter.statusPagamento())) {
            try {
                StatusPagamento.valueOf(filter.statusPagamento().trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new RegraNegocioException("Status de pagamento inválido. Use: Pago, Pendente ou Cancelado.");
            }
        }
    }

    private String formatarTempoPermanencia(LocalDateTime entrada, LocalDateTime saida) {
        if (entrada == null || saida == null) {
            return "00:00";
        }
        return formatarDuracaoSegundos(Duration.between(entrada, saida).getSeconds());
    }

    private String formatarDuracaoSegundos(long segundos) {
        if (segundos < 0) {
            segundos = 0;
        }
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        return String.format("%02d:%02d", horas, minutos);
    }

    private BigDecimal resolverValorCobrado(Registro registro) {
        if (registro.getValorCobrado() != null) {
            return registro.getValorCobrado();
        }
        if (registro.getTaxaVeiculo() != null) {
            return registro.getTaxaVeiculo();
        }
        return BigDecimal.ZERO;
    }

    private String resolverStatusPagamento(Registro registro) {
        if (registro.getStatusPagamento() == null) {
            return StatusPagamento.PENDENTE.getDescricao();
        }
        return registro.getStatusPagamento().getDescricao();
    }

    private boolean isStatusPagamento(Registro registro, StatusPagamento status) {
        if (registro.getStatusPagamento() == null) {
            return status == StatusPagamento.PENDENTE;
        }
        return registro.getStatusPagamento() == status;
    }
}
