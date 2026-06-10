package br.com.mercadoprodutor.service;

import java.util.List;

import br.com.mercadoprodutor.models.Comprador;
import br.com.mercadoprodutor.models.PerfilUsuario;
import br.com.mercadoprodutor.models.Produtor;
import br.com.mercadoprodutor.repositories.CompradorRepository;
import br.com.mercadoprodutor.repositories.ProdutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.mercadoprodutor.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.models.Usuario;
import br.com.mercadoprodutor.repositories.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private ProdutorRepository produtorRepository;

    @Autowired
    private CompradorRepository compradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto) {

        if (repository.findByEmail(dto.email()) != null) {
            throw new RuntimeException("Já existe um usuário cadastrado com este e-mail.");
        }

        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            if (produtorRepository.findByCpf(dto.cpf()) != null || compradorRepository.findByCpf(dto.cpf()) != null) {
                throw new RuntimeException("Já existe um cadastro com este CPF.");
            }
        }

        String senhaCriptografada =
                passwordEncoder.encode(dto.senha());

        Usuario usuario = new Usuario(
                dto.nome(),
                dto.email(),
                senhaCriptografada,
                dto.perfil()
        );

        Usuario usuarioSalvo = repository.save(usuario);

        if (dto.perfil() == PerfilUsuario.PRODUTOR) {
            Produtor produtor = new Produtor(dto.cpf(), dto.telefone(), usuarioSalvo);
            produtorRepository.save(produtor);
        } else if (dto.perfil() == PerfilUsuario.COMPRADOR) {
            Comprador comprador = new Comprador(dto.cpf(), dto.telefone(), usuarioSalvo);
            compradorRepository.save(comprador);
        }

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getStatusAcesso(),
                usuarioSalvo.getPerfis()
        );
    }

    public List<UsuarioResponseDTO> listarUsuarios() {

        return repository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getStatusAcesso(),
                        usuario.getPerfis()
                ))
                .toList();
    }
}
