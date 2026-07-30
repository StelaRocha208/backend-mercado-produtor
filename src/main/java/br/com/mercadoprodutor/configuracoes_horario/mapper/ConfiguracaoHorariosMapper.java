package br.com.mercadoprodutor.configuracoes_horario.mapper;

import br.com.mercadoprodutor.configuracoes_horario.dto.ConfiguracaoHorariosDTO;
import br.com.mercadoprodutor.configuracoes_horario.model.ConfiguracaoHorarios;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoHorariosMapper {

    public ConfiguracaoHorariosDTO toDto(ConfiguracaoHorarios entity) {
        ConfiguracaoHorariosDTO dto = new ConfiguracaoHorariosDTO();
        dto.setDiaDom(entity.getDiaDom());
        dto.setDiaSeg(entity.getDiaSeg());
        dto.setDiaTer(entity.getDiaTer());
        dto.setDiaQua(entity.getDiaQua());
        dto.setDiaQui(entity.getDiaQui());
        dto.setDiaSex(entity.getDiaSex());
        dto.setDiaSab(entity.getDiaSab());
        dto.setHorarioCompradorInicio(entity.getHorarioCompradorInicio());
        dto.setHorarioCompradorFim(entity.getHorarioCompradorFim());
        dto.setHorarioProdutorInicio(entity.getHorarioProdutorInicio());
        dto.setHorarioProdutorFim(entity.getHorarioProdutorFim());
        dto.setPrazoAntecedenciaDias(entity.getPrazoAntecedenciaDias());
        dto.setExpiracaoReservaHoras(entity.getExpiracaoReservaHoras());
        dto.setLembreteReservaHoras(entity.getLembreteReservaHoras());
        return dto;
    }

    public void updateEntityFromDto(ConfiguracaoHorariosDTO dto, ConfiguracaoHorarios entity) {
        entity.setDiaDom(dto.getDiaDom());
        entity.setDiaSeg(dto.getDiaSeg());
        entity.setDiaTer(dto.getDiaTer());
        entity.setDiaQua(dto.getDiaQua());
        entity.setDiaQui(dto.getDiaQui());
        entity.setDiaSex(dto.getDiaSex());
        entity.setDiaSab(dto.getDiaSab());
        entity.setHorarioCompradorInicio(dto.getHorarioCompradorInicio());
        entity.setHorarioCompradorFim(dto.getHorarioCompradorFim());
        entity.setHorarioProdutorInicio(dto.getHorarioProdutorInicio());
        entity.setHorarioProdutorFim(dto.getHorarioProdutorFim());
        entity.setPrazoAntecedenciaDias(dto.getPrazoAntecedenciaDias());
        entity.setExpiracaoReservaHoras(dto.getExpiracaoReservaHoras());
        entity.setLembreteReservaHoras(dto.getLembreteReservaHoras());
    }

}