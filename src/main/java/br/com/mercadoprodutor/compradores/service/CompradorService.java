package br.com.mercadoprodutor.compradores.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.compradores.dto.CompradorCreateDTO;
import br.com.mercadoprodutor.compradores.model.Comprador;
import br.com.mercadoprodutor.compradores.repository.CompradorRepository;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import br.com.mercadoprodutor.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompradorService implements ICompradorService {

    private final CompradorRepository compradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void criarComprador(CompradorCreateDTO dto) {

        // Validações de Regra de Negócio
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("O e-mail informado já está em uso.");
        }
        if (compradorRepository.existsByCpf(dto.cpf())) {
            throw new RegraNegocioException("O CPF informado já está cadastrado.");
        }

        // Cria a entidade base (Usuario) com o Perfil COMPRADOR
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
                dto.nome(),
                dto.email(),
                senhaCriptografada,
                Set.of(PerfilUsuario.COMPRADOR)
        );

        usuarioRepository.save(novoUsuario);

        // Cria a entidade Comprador vinculada ao Usuário
        Comprador novoComprador = new Comprador(dto.cpf(), dto.telefone(), novoUsuario);

        compradorRepository.save(novoComprador);
    }
}
