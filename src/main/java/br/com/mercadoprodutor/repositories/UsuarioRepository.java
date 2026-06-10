package br.com.mercadoprodutor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.mercadoprodutor.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String>{

    @Query("SELECT u FROM usuarios u " +
            "LEFT JOIN produtores p ON p.usuario.id = u.id " +
            "LEFT JOIN compradores c ON c.usuario.id = u.id " +
            "WHERE u.email = :loginValue OR p.cpf = :loginValue OR c.cpf = :loginValue")
    UserDetails findByEmailOrCpf(@Param("loginValue") String loginValue);

    UserDetails findByEmail(String email);
}