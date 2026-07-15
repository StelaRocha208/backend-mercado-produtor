package br.com.mercadoprodutor.portaria.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.compradores.model.Comprador;
import br.com.mercadoprodutor.compradores.repository.CompradorRepository;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.portaria.dto.BuscaPortariaResponseDTO;
import br.com.mercadoprodutor.portaria.dto.LiberacaoExcepcionalDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroSaidaDTO;
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

        Veiculo veiculo = veiculoRepository.findByPlaca(valor)
                .orElseThrow(() ->
                        new RegraNegocioException("Veículo não encontrado."));

        return montarRespostaProdutor(veiculo.getProdutor());
    }

    @Override
    @Transactional
    public void liberarAcesso(LiberacaoExcepcionalDTO dto) {

        Produtor produtor = produtorRepository.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() ->
                        new RegraNegocioException("Produtor não encontrado."));

        if (!produtor.getInadimplente()) {
            throw new RegraNegocioException(
                    "O produtor não possui pendências.");
        }

        if (dto.justificativa() == null || dto.justificativa().isBlank()) {
            throw new RegraNegocioException(
                    "Informe uma justificativa para liberar o acesso.");
        }

        produtor.setJustificativaInadimplencia(dto.justificativa());
        produtor.setLiberacaoExcepcional(true);

        produtorRepository.save(produtor);
    }

    @Override
    @Transactional
    public RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto) {

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

        if (!veiculo.getProdutor().getId().equals(produtor.getId())) {
            throw new RegraNegocioException(
                    "O veículo informado não pertence ao produtor selecionado.");
        }

        if (produtor.getInadimplente()
                && !Boolean.TRUE.equals(produtor.getLiberacaoExcepcional())) {

            throw new RegraNegocioException(
                    "Produtor inadimplente. É necessária autorização excepcional.");
        }

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
         * Salva no histórico do registro
         */

        if (Boolean.TRUE.equals(produtor.getLiberacaoExcepcional())) {

            registro.setLiberacaoExcepcional(true);
            registro.setJustificativaLiberacao(
                    produtor.getJustificativaInadimplencia()
            );
            registro.setDataLiberacao(LocalDateTime.now());

            /*
             * Limpa o produtor para futuras visitas
             */

            produtor.setLiberacaoExcepcional(false);
            produtor.setJustificativaInadimplencia(null);

            produtorRepository.save(produtor);
        }

        /*
         * TODO
         * Quando o módulo Reserva estiver implementado,
         * o Registro será vinculado à Reserva.
         */

        registro = registroRepository.save(registro);

        return new RegistroResponseDTO(
                registro,
                produtor.getUsuario().getPerfis().name()
        );
    }

    @Override
    @Transactional
    public RegistroResponseDTO registrarSaida(RegistroSaidaDTO dto) {

        Registro registro = registroRepository.findById(dto.registroId())
                .orElseThrow(() ->
                        new RegraNegocioException("Registro não encontrado."));

        if (registro.getStatusRegistro() == StatusRegistro.ENCERRADO) {
            throw new RegraNegocioException(
                    "Este registro já foi encerrado.");
        }

        Produtor produtor = registro.getProdutor();

        registro.setDataSaida(LocalDateTime.now());
        registro.setStatusRegistro(StatusRegistro.ENCERRADO);

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
                produtor.getInadimplente(),
                produtor.getJustificativaInadimplencia(),
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
                false,
                null,
                List.of(comprador.getUsuario().getPerfis().name()),
                List.of()
        );
    }
}