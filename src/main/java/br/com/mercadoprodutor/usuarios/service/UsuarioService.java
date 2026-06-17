package br.com.mercadoprodutor.usuarios.service;

import java.util.List;

import br.com.mercadoprodutor.audit.aspect.AuditarAcao;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.usuarios.dto.UsuarioUpdateDTO;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import br.com.mercadoprodutor.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mercadoprodutor.usuarios.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.usuarios.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @AuditarAcao(valor = "Usuario Criado") //Anotação custom de auditoria
    public UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto) {

        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usuarioLogado.getPerfis() == PerfilUsuario.OPERADOR && dto.perfil() == PerfilUsuario.ADMINISTRADOR) {
            throw new RegraNegocioException("Operadores não têm permissão para cadastrar novos Administradores.");
        }

        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("O e-mail informado já está em uso no sistema.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), senhaCriptografada, dto.perfil());

        repository.save(novoUsuario);
        return new UsuarioResponseDTO(novoUsuario);
    }

    @Override
    @Transactional
    @AuditarAcao(valor = "Alteração de Cadastro de Usuário")
    public UsuarioResponseDTO editarUsuario(String id, UsuarioUpdateDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        usuario.setNome(dto.nome());
        usuario.setStatusAcesso(dto.statusAcesso());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        return new UsuarioResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(String busca, Pageable pageable) {
        return repository.buscarComFiltro(busca, pageable).map(UsuarioResponseDTO::new);
    }

}
