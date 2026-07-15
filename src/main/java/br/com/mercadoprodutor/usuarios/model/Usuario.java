package br.com.mercadoprodutor.usuarios.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.mercadoprodutor.core.model.BaseEntity;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_perfis", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil")
    private Set<PerfilUsuario> perfis = new HashSet<>();

    @Column(name = "perfil_ativo")
    @Setter
    private String perfilAtivo;

    public Usuario(String nome, String email, String senha, Set<PerfilUsuario> perfis) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfis = perfis;
        this.statusAcesso = "ATIVO";
        this.perfilAtivo = perfis.isEmpty() ? null : perfis.iterator().next().name();
    }

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfis.contains(PerfilUsuario.ADMINISTRADOR)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }

        if (this.perfilAtivo != null && !this.perfilAtivo.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + this.perfilAtivo.toUpperCase()));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));

    }

    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }
}
