package br.com.mercadoprodutor.auth.service;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.usuarios.model.PerfilUsuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import br.com.mercadoprodutor.usuarios.model.Usuario;


import br.com.mercadoprodutor.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserDetails userDetails = repository.findByEmailOrCpf(login);

        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com a credencial: " + login);
        }

        Usuario usuario = (Usuario) userDetails;
        if ("INATIVO".equals(usuario.getStatusAcesso())) {
            throw new RegraNegocioException("Acesso negado: Usuário inativo no sistema.");
        }

        return userDetails;
    }

    //Injeção necessária do TokenService
    private final br.com.mercadoprodutor.auth.service.TokenService tokenService;

    public String alternarPerfilAtivo(String email, String novoPerfilAtivo) {
        //Busca o usuário
        Usuario usuario = (Usuario) repository.findByEmailOrCpf(email);
        if (usuario == null) {
            throw new RegraNegocioException("Usuário não encontrado.");
        }

        // Converte a string para o Enum
        PerfilUsuario perfilAlvo;
        try {
            perfilAlvo = PerfilUsuario.valueOf(novoPerfilAtivo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Perfil inválido.");
        }

        // Verifica se o usuário tem esse perfil na lista dele
        if (!usuario.getPerfis().contains(perfilAlvo)) {
            throw new RegraNegocioException("O usuário não possui permissão para alternar para este perfil.");
        }

        //Atualiza e salva
        usuario.setPerfilAtivo(novoPerfilAtivo);
        repository.save(usuario);

        //Gera o novo token
        return tokenService.generateToken(usuario);

    }

}
