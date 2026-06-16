package br.com.mercadoprodutor.portaria.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.portaria.dto.RegistroEntradaDTO;
import br.com.mercadoprodutor.portaria.dto.RegistroResponseDTO;
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
    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional
    public RegistroResponseDTO registrarEntrada(RegistroEntradaDTO dto) {

        Produtor produtor = produtorRepository.findById(dto.produtorId())
                .orElseThrow(() ->
                        new RegraNegocioException("Produtor não encontrado."));

        Veiculo veiculo = veiculoRepository.findById(dto.veiculoId())
                .orElseThrow(() ->
                        new RegraNegocioException("Veículo não encontrado."));

        registroRepository
                .findByProdutorIdAndStatusRegistro(
                        produtor.getId(),
                        StatusRegistro.EM_ANDAMENTO
                )
                .ifPresent(registro -> {
                    throw new RegraNegocioException(
                            "O produtor já possui uma visita em andamento.");
                });

        Registro novoRegistro = new Registro();

        novoRegistro.setProdutor(produtor);
        novoRegistro.setVeiculo(veiculo);

        novoRegistro.setSecao(dto.secao());
        novoRegistro.setEspaco(dto.espaco());

        novoRegistro.setNumeroNF(dto.numeroNF());
        novoRegistro.setObservacao(dto.observacao());

        novoRegistro.setDataEntrada(LocalDateTime.now());

        novoRegistro.setStatusRegistro(StatusRegistro.EM_ANDAMENTO);

        registroRepository.save(novoRegistro);

        return new RegistroResponseDTO(novoRegistro);
    }
}