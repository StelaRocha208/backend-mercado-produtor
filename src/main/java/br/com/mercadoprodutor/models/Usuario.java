package br.com.mercadoprodutor.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuarios")
@Entity(name = "usuarios")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String statusAcesso;

    @Enumerated(EnumType.ORDINAL)
private PerfilUsuario perfis;

    private String perfilAtivo;

    // Construtor já utilizado pelo AuthenticationController
    public Usuario(String email, String senha, PerfilUsuario perfil) {
        this.email = email;
        this.senha = senha;
        this.perfis = perfil;
    }

    // Novo construtor para o CRUD de usuários
    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfis = perfil;
        this.statusAcesso = "ATIVO";
        this.perfilAtivo = perfil.name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfis == PerfilUsuario.ADMINISTRADOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}