package br.com.mercadoprodutor.reservas.service;

import br.com.mercadoprodutor.espacos.model.TipoSecao;
import br.com.mercadoprodutor.reservas.model.TipoReserva;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/** Define os limites de vigência concedidos pelo administrador. */
@Component
public class PoliticaPeriodoReserva {

    private static final int VIGENCIA_ANOS_BOX_PEDRA = 5;
    private static final int VIGENCIA_MESES_VOLATIL = 1;

    public LocalDate calcularDataFim(
            TipoReserva tipoReserva,
            TipoSecao tipoSecao,
            LocalDate dataInicio,
            LocalDate dataFimInformada
    ) {
        validarTipoPermitido(tipoReserva, tipoSecao);

        if (tipoReserva == TipoReserva.DIARIA) {
            return calcularDataFimDiaria(dataInicio, dataFimInformada);
        }

        LocalDate dataFimLimite = switch (tipoReserva) {
            case TITULAR -> dataInicio
                    .plusYears(VIGENCIA_ANOS_BOX_PEDRA)
                    .minusDays(1);
            case PRIORITARIA -> dataInicio
                    .plusMonths(VIGENCIA_MESES_VOLATIL)
                    .minusDays(1);
            case DIARIA, PROVISORIA -> throw requisicaoInvalida(
                    "Tipo de reserva inválido para criação administrativa."
            );
        };

        LocalDate dataFim = dataFimInformada == null
                ? dataFimLimite
                : dataFimInformada;

        if (dataFim.isBefore(dataInicio)) {
            throw requisicaoInvalida(
                    "A data final não pode ser anterior à data inicial."
            );
        }

        if (dataFim.isAfter(dataFimLimite)) {
            String limite = tipoReserva == TipoReserva.PRIORITARIA
                    ? "um mês"
                    : "cinco anos";
            throw requisicaoInvalida(
                    "O período máximo para este tipo de espaço é de "
                            + limite + "."
            );
        }

        return dataFim;
    }

    private LocalDate calcularDataFimDiaria(
            LocalDate dataInicio,
            LocalDate dataFimInformada
    ) {
        LocalDate dataFim = dataFimInformada == null
                ? dataInicio
                : dataFimInformada;

        if (!dataFim.equals(dataInicio)) {
            throw requisicaoInvalida(
                    "A reserva diária deve começar e terminar no mesmo dia."
            );
        }

        return dataFim;
    }

    private void validarTipoPermitido(
            TipoReserva tipoReserva,
            TipoSecao tipoSecao
    ) {
        if (tipoReserva == null) {
            throw requisicaoInvalida("O tipo da reserva é obrigatório.");
        }

        boolean permitido = switch (tipoSecao) {
            case BOX, PEDRA -> tipoReserva == TipoReserva.TITULAR;
            case VOLATIL -> TipoReserva.reservasVolateis().contains(tipoReserva);
        };

        if (!permitido) {
            throw requisicaoInvalida(
                    "Box e Pedra aceitam titularidade; espaços voláteis aceitam "
                            + "reservas diárias ou prioritárias."
            );
        }
    }

    private ResponseStatusException requisicaoInvalida(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
