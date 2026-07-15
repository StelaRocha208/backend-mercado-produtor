package br.com.mercadoprodutor.usuarios.service;

import java.util.List;

import br.com.mercadoprodutor.audit.aspect.AuditarAcao;
import br.com.mercadoprodutor.compradores.dto.CompradorDetalhesDTO;
import br.com.mercadoprodutor.compradores.model.Comprador;
import br.com.mercadoprodutor.compradores.repository.CompradorRepository;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.produtores.dto.ProdutorDetalhesDTO;
import br.com.mercadoprodutor.produtores.dto.VeiculoResponseDTO;
import br.com.mercadoprodutor.produtores.model.Produtor;
import br.com.mercadoprodutor.produtores.repository.ProdutorRepository;
import br.com.mercadoprodutor.usuarios.dto.UsuarioDetalhesDTO;
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
    private final CompradorRepository compradorRepository;
    private final ProdutorRepository produtorRepository;

    @Override
    @Transactional
    @AuditarAcao(valor = "Usuario Criado") //Anotação custom de auditoria
    public UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto) {

        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usuarioLogado.getPerfis().contains(PerfilUsuario.OPERADOR) && dto.perfis().contains(PerfilUsuario.ADMINISTRADOR)) {
            throw new RegraNegocioException("Operadores não têm permissão para cadastrar novos Administradores.");
        }

        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("O e-mail informado já está em uso no sistema.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), senhaCriptografada, dto.perfis());

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


    @Override
    @Transactional(readOnly = true)
    public Object buscarDetalhesUsuario(String id) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        // 1. Caso seja Administrador ou Operador
        if (usuario.getPerfis().contains(PerfilUsuario.ADMINISTRADOR) ||
                usuario.getPerfis().contains(PerfilUsuario.OPERADOR)) {
            return new UsuarioDetalhesDTO(
                    "USUARIO",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getStatusAcesso(),
                    usuario.getPerfis(),
                    usuario.getDataCriacao().toString()
            );
        }

        // 2. Tenta buscar como Comprador
        if (usuario.getPerfis().contains(PerfilUsuario.COMPRADOR)) {
            return compradorRepository.findByUsuarioId(id)
                    .map(c -> new CompradorDetalhesDTO(
                            "COMPRADOR",
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getEmail(),
                            c.getCpf(),
                            c.getTelefone(),
                            usuario.getStatusAcesso(),
                            usuario.getPerfis(),
                            usuario.getDataCriacao().toString()
                    ))
                    // Se não encontrar o comprador, retorna os dados básicos em vez de dar erro 400
                    .orElse(new CompradorDetalhesDTO(
                            "COMPRADOR", usuario.getId(), usuario.getNome(), usuario.getEmail(),
                            "N/A", "N/A", usuario.getStatusAcesso(), usuario.getPerfis(), usuario.getDataCriacao().toString()
                    ));
        }

        // 3. Tenta buscar como Produtor
        return produtorRepository.findByUsuarioId(id)
                .map(p -> new ProdutorDetalhesDTO(
                        "PRODUTOR",
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        p.getCpf(),
                        p.getTelefone(),
                        usuario.getStatusAcesso(),
                        usuario.getPerfis(),
                        usuario.getDataCriacao().toString(),
                        p.getVeiculos().stream()
                                .map(v -> new VeiculoResponseDTO(v.getPlaca(), v.getTipo(), v.getMarca(), v.getModelo(), v.getCor()))
                                .toList()
                ))
                // Se não encontrar o produtor, retorna os dados básicos em vez de dar erro 400
                .orElse(new ProdutorDetalhesDTO(
                        "PRODUTOR", usuario.getId(), usuario.getNome(), usuario.getEmail(),
                        "N/A", "N/A", usuario.getStatusAcesso(), usuario.getPerfis(), usuario.getDataCriacao().toString(), List.of()
                ));
        }
    }

