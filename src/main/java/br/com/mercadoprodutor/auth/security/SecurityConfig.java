package br.com.mercadoprodutor.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rotas de Autenticação (Públicas)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                        // Rotas de Cadastro de Usuários (Públicas)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/produtores").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/compradores").permitAll()

                        // Liberação temporária para testes do GET no painel administrativo
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/audit").permitAll()
                        
                        // LIBERAÇÃO TEMPORÁRIA DA PORTARIA
                        .requestMatchers(HttpMethod.GET, "/portaria/busca").permitAll()
                        .requestMatchers(HttpMethod.POST, "/portaria/entrada").permitAll()
                        .requestMatchers(HttpMethod.POST, "/portaria/saida").permitAll()

                        // Liberação temporária para testes do mapa
                        .requestMatchers(HttpMethod.GET, "/api/secoes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ocupacao/**").permitAll()



                        // Liberação temporária para testes do mapa
                        .requestMatchers(HttpMethod.GET, "/api/secoes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ocupacao/**").permitAll()
                        


                        // Liberação temporária para testes do mapa
                        .requestMatchers(HttpMethod.GET, "/api/secoes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ocupacao/**").permitAll()
                        


                        // Liberação temporária para testes do mapa
                        .requestMatchers(HttpMethod.GET, "/api/secoes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/espacos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ocupacao/**").permitAll()
                        


                        // Rotas de configuração para redefinição de senha
                        .requestMatchers(HttpMethod.POST, "/auth/esqueci-senha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/redefinir-senha").permitAll()

                        .requestMatchers("/error").permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/usuarios/*/detalhes"
                        ).permitAll()

                        // Qualquer outra rota precisa de token (ex: PUT, DELETE, Dashboard)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
