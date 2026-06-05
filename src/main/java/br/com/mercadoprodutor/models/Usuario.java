package br.com.mercadoprodutor.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
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
public class Usuario implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String statusAcesso;
    private PerfilUsuario perfis;
    private String perfilAtivo;

    public Usuario(String email, String senha, PerfilUsuario perfil){
        this.email = email;
        this.senha = senha;
        this.perfis = perfil;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //to do: Implementar para todos os tipos de Perfis ex: comprador, operador, produtor;
        if(this.perfis == PerfilUsuario.ADMINISTRADOR) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
