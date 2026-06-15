package br.com.mercadoprodutor.usuarios.model;

import java.util.Collection;
import java.util.List;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.mercadoprodutor.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@NoArgsConstructor
public class Usuario extends BaseEntity implements UserDetails {

    @Column(nullable = false)
    @Setter private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Setter private String senha;

    @Column(name = "status_acesso", nullable = false)
    @Setter private String statusAcesso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfis;

    @Column(name = "perfil_ativo")
    private String perfilAtivo;

    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfis = perfil;
        this.statusAcesso = "ATIVO";
        this.perfilAtivo = perfil.name();
    }

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfis == PerfilUsuario.ADMINISTRADOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }
}
