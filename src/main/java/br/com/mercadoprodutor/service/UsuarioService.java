package br.com.mercadoprodutor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.mercadoprodutor.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.models.Usuario;
import br.com.mercadoprodutor.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto) {

        if (repository.findByEmail(dto.email()) != null) {
            throw new RuntimeException("Já existe um usuário cadastrado com este e-mail.");
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
