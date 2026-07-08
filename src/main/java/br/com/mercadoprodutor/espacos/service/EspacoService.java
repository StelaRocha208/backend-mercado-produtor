package br.com.mercadoprodutor.espacos.service;

import br.com.mercadoprodutor.espacos.dto.EspacoResponse;
import br.com.mercadoprodutor.espacos.dto.OcupacaoResumoResponse;
import br.com.mercadoprodutor.espacos.dto.SecaoResponse;
import br.com.mercadoprodutor.espacos.model.Espaco;
import br.com.mercadoprodutor.espacos.model.StatusOcupacao;
import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.espacos.repository.EspacoRepository;
import br.com.mercadoprodutor.espacos.repository.SecaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EspacoService {

    private final SecaoRepository secaoRepository;
    private final EspacoRepository espacoRepository;

    @Transactional(readOnly = true)
    public List<SecaoResponse> listarSecoes() {
        return secaoRepository.findByAtivaTrueOrderByOrdemVisualAsc()
            .stream()
            .map(SecaoResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EspacoResponse> listarEspacos(TipoSecao secao) {
        List<Espaco> espacos = secao == null
            ? espacoRepository.findAllAtivosOrdenados()
            : espacoRepository.findAtivosByTipoSecao(secao);

        return espacos.stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OcupacaoResumoResponse> obterResumoOcupacao() {
        List<Espaco> espacos = espacoRepository.findAllAtivosOrdenados();

        Map<TipoSecao, List<Espaco>> porSecao = espacos.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                espaco -> espaco.getSecao().getTipo(),
                () -> new EnumMap<>(TipoSecao.class),
                java.util.stream.Collectors.toList()
            ));

        return porSecao.entrySet()
            .stream()
            .map(entry -> montarResumo(entry.getKey(), entry.getValue()))
            .toList();
    }

    private OcupacaoResumoResponse montarResumo(TipoSecao secao, List<Espaco> espacos) {
        int livres = contarPorStatus(espacos, StatusOcupacao.LIVRE);
        int reservados = contarPorStatus(espacos, StatusOcupacao.RESERVADO);
        int ocupados = contarPorStatus(espacos, StatusOcupacao.OCUPADO);
        int indisponiveis = contarPorStatus(espacos, StatusOcupacao.INDISPONIVEL);

        return new OcupacaoResumoResponse(
            secao,
            espacos.size(),
            livres,
            reservados,
            ocupados,
            indisponiveis
        );
    }

    private int contarPorStatus(List<Espaco> espacos, StatusOcupacao status) {
        return (int) espacos.stream()
            .filter(espaco -> espaco.getStatusOcupacao() == status)
            .count();
    }

    private EspacoResponse toResponse(Espaco espaco) {
        return EspacoResponse.fromEntity(espaco);
    }
}