package br.com.mercadoprodutor.auth.service;

import br.com.mercadoprodutor.core.exception.RegraNegocioException;
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
}
