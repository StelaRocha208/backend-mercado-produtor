package br.com.mercadoprodutor.portaria.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.compradores.model.Comprador;
import br.com.mercadoprodutor.compradores.repository.CompradorRepository;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.portaria.dto.BuscaPortariaResponseDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
import br.com.mercadoprodutor.portaria.dto.VeiculoPortariaDTO;
import br.com.mercadoprodutor.portaria.model.Registro;
import br.com.mercadoprodutor.portaria.model.StatusRegistro;
import br.com.mercadoprodutor.portaria.repository.RegistroRepository;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.model.Veiculo;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.produtores.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroService implements IRegistroService {

    private final RegistroRepository registroRepository;
    private final ProdutorRepository produtorRepository;
    private final CompradorRepository compradorRepository;
    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional(readOnly = true)
    public BuscaPortariaResponseDTO buscarUsuario(String valor) {

        // Busca por CPF
        if (valor.matches("\\d{11}")) {

            Produtor produtor = produtorRepository.findByCpf(valor).orElse(null);

            if (produtor != null) {
                return montarRespostaProdutor(produtor);
            }

            Comprador comprador = compradorRepository.findByCpf(valor).orElse(null);

            if (comprador != null) {
                return montarRespostaComprador(comprador);
            }

            throw new RegraNegocioException("Usuário não encontrado.");
        }

        // Busca por placa
        Veiculo veiculo = veiculoRepository.findByPlaca(valor)
                .orElseThrow(() ->
                        new RegraNegocioException("Veículo não encontrado."));

        return montarRespostaProdutor(veiculo.getProdutor());
    }

    @Override
    @Transactional
    public RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto) {

        // Nesta sprint o registro de entrada está preparado apenas para PRODUTOR.
        // O fluxo de Comprador e Reserva será concluído quando o módulo de
        // Reserva estiver implementado.
        if (!"PRODUTOR".equalsIgnoreCase(dto.perfil())) {
            throw new RegraNegocioException(
                    "No momento o registro de entrada está disponível apenas para produtores.");
        }

        Produtor produtor = produtorRepository.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() ->
                        new RegraNegocioException("Produtor não encontrado."));

        Veiculo veiculo = veiculoRepository.findById(dto.veiculoId())
                .orElseThrow(() ->
                        new RegraNegocioException("Veículo não encontrado."));

        // Verifica se o veículo pertence ao produtor selecionado
        if (!veiculo.getProdutor().getId().equals(produtor.getId())) {
            throw new RegraNegocioException(
                    "O veículo informado não pertence ao produtor selecionado.");
        }

        // Não permite dois registros em andamento
        registroRepository
                .findByProdutorIdAndStatusRegistro(
                        produtor.getId(),
                        StatusRegistro.EM_ANDAMENTO
                )
                .ifPresent(registro -> {
                    throw new RegraNegocioException(
                            "O usuário já possui um registro em andamento.");
                });

        Registro registro = new Registro();

        registro.setProdutor(produtor);
        registro.setVeiculo(veiculo);

        registro.setDataEntrada(LocalDateTime.now());
        registro.setStatusRegistro(StatusRegistro.EM_ANDAMENTO);

        /*
         * TODO
         * Quando o módulo de Reserva estiver implementado,
         * o Registro receberá a Reserva vinculada.
         *
         * Também voltarão a ser preenchidos:
         * - Seção
         * - Espaço
         * - Nota Fiscal
         */

        registro = registroRepository.save(registro);

        return new RegistroResponseDTO(
        registro,
        produtor.getUsuario().getPerfis().name()
);
    }

    private BuscaPortariaResponseDTO montarRespostaProdutor(Produtor produtor) {

        List<VeiculoPortariaDTO> veiculos = produtor.getVeiculos()
                .stream()
                .map(v -> new VeiculoPortariaDTO(
                        v.getId(),
                        v.getPlaca(),
                        v.getTipo()
                ))
                .toList();

        return new BuscaPortariaResponseDTO(
                produtor.getUsuario().getId(),
                produtor.getUsuario().getNome(),
                produtor.getCpf(),
                produtor.getTelefone(),
                "ATIVO".equalsIgnoreCase(produtor.getUsuario().getStatusAcesso()),
                List.of(produtor.getUsuario().getPerfis().name()),
                veiculos
        );
    }

    private BuscaPortariaResponseDTO montarRespostaComprador(Comprador comprador) {

        return new BuscaPortariaResponseDTO(
                comprador.getUsuario().getId(),
                comprador.getUsuario().getNome(),
                comprador.getCpf(),
                comprador.getTelefone(),
                "ATIVO".equalsIgnoreCase(comprador.getUsuario().getStatusAcesso()),
                List.of(comprador.getUsuario().getPerfis().name()),
                List.of()
        );
    }
}