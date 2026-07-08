package br.com.mercadoprodutor.usuarios.service;

import br.com.mercadoprodutor.usuarios.dto.UsuarioCreateDTO;
import br.com.mercadoprodutor.usuarios.dto.UsuarioResponseDTO;
import br.com.mercadoprodutor.usuarios.dto.UsuarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUsuarioService {
    UsuarioResponseDTO criarUsuario(UsuarioCreateDTO dto);
    UsuarioResponseDTO editarUsuario(String id, UsuarioUpdateDTO dto);
    Page<UsuarioResponseDTO> listarUsuarios(String busca, Pageable pageable);
    Object buscarDetalhesUsuario(String id);
}
