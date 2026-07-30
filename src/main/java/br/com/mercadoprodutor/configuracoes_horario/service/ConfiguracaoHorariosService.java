package br.com.mercadoprodutor.configuracoes_horario.service;

import br.com.mercadoprodutor.configuracoes_horario.dto.ConfiguracaoHorariosDTO;
import br.com.mercadoprodutor.configuracoes_horario.mapper.ConfiguracaoHorariosMapper;
import br.com.mercadoprodutor.configuracoes_horario.model.ConfiguracaoHorarios;
import br.com.mercadoprodutor.configuracoes_horario.repository.ConfiguracaoHorariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracaoHorariosService {

    private final ConfiguracaoHorariosRepository repository;
    private final ConfiguracaoHorariosMapper mapper;

    public ConfiguracaoHorariosDTO getConfiguracoes() {
        ConfiguracaoHorarios config = repository.findById("DEFAULT").orElseGet(() -> {
            return repository.save(new ConfiguracaoHorarios());
        });
        return mapper.toDto(config);
    }

    public ConfiguracaoHorariosDTO atualizarConfiguracoes(ConfiguracaoHorariosDTO dto) {
        ConfiguracaoHorarios configAtual = repository.findById("DEFAULT").orElseGet(ConfiguracaoHorarios::new);

        mapper.updateEntityFromDto(dto, configAtual);
        configAtual.setId("DEFAULT");

        ConfiguracaoHorarios salvo = repository.save(configAtual);
        return mapper.toDto(salvo);
    }

}