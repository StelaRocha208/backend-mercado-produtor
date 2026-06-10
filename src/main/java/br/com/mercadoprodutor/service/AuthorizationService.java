package br.com.mercadoprodutor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.mercadoprodutor.repositories.UsuarioRepository;

@Service
public class AuthorizationService implements UserDetailsService {
    @Autowired
    UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = repository.findByEmailOrCpf(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com a credencial: " + username);
        }
        return user;
    }
}
