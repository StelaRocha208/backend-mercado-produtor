package br.com.mercadoprodutor.produtores.service;

import br.com.mercadoprodutor.produtores.service.IProdutorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.produtores.dto.ProdutorCreateDTO;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.model.Veiculo;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import br.com.mercadoprodutor.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutorService implements IProdutorService {

    private final ProdutorRepository produtorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void criarProdutor(ProdutorCreateDTO dto) {
        // Validações de duplicidade
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("O e-mail informado já está em uso.");
        }
        if (produtorRepository.existsByCpf(dto.cpf())) {
            throw new RegraNegocioException("O CPF informado já está cadastrado.");
        }

        // Criar Entidade Usuario
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
                dto.nome(),
                dto.email(),
                senhaCriptografada,
                PerfilUsuario.PRODUTOR
        );
        usuarioRepository.save(novoUsuario);

        // Criar Entidade Produtor vinculada ao Usuário
        Produtor novoProdutor = new Produtor(dto.cpf(), dto.telefone(), novoUsuario);

        // Criar e vincular o Veículo apenas se foi enviado pelo frontend
        if (dto.veiculoPrincipal() != null) {
            Veiculo veiculo = new Veiculo(
                    dto.veiculoPrincipal().placa(),
                    dto.veiculoPrincipal().tipo(),
                    dto.veiculoPrincipal().marca(),
                    dto.veiculoPrincipal().modelo(),
                    dto.veiculoPrincipal().cor(),
                    novoProdutor
            );
            novoProdutor.adicionarVeiculo(veiculo);
        }

        // Salva Produtor (e por cascata, salvará o veículo)
        produtorRepository.save(novoProdutor);
    }
}
