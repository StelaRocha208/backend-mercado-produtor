package br.com.mercadoprodutor.usuarios.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import br.com.mercadoprodutor.usuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    UserDetails findByEmail(String email);
    boolean existsByEmail(String email);

    // O LEFT JOIN procura o CPF nas tabelas filhas se o usuário for Produtor ou Comprador
    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN Produtor p ON p.usuario = u " +
            "LEFT JOIN Comprador c ON c.usuario = u " +
            "WHERE u.email = :login OR p.cpf = :login OR c.cpf = :login")
    UserDetails findByEmailOrCpf(@Param("login") String login);

    // Filtra por nome ou e-mail com paginação
    @Query("SELECT u FROM Usuario u WHERE " +
            "(:busca IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Usuario> buscarComFiltro(@Param("busca") String busca, Pageable pageable);
}