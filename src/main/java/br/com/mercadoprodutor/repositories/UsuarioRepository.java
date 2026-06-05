package br.com.mercadoprodutor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.mercadoprodutor.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String>{
    UserDetails findByEmail(String email);
}