package br.com.mercadoprodutor.auth.repository;

import br.com.mercadoprodutor.auth.model.RedefinicaoSenhaToken;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RedefinicaoSenhaTokenRepository extends JpaRepository<RedefinicaoSenhaToken, String> {
    Optional<RedefinicaoSenhaToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario); // Para limpar tokens antigos
}
