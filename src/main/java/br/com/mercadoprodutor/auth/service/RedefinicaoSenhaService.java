package br.com.mercadoprodutor.auth.service;

import br.com.mercadoprodutor.auth.model.RedefinicaoSenhaToken;
import br.com.mercadoprodutor.auth.repository.RedefinicaoSenhaTokenRepository;
import br.com.mercadoprodutor.core.exception.RegraNegocioException;
import br.com.mercadoprodutor.usuarios.model.Usuario;
import br.com.mercadoprodutor.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedefinicaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final RedefinicaoSenhaTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void solicitarRecuperacao(String login) {
        // Busca o usuário pelo E-mail ou CPF
        Usuario usuario = usuarioRepository.findUsuarioOptionalByEmailOrCpf(login)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado com os dados fornecidos."));

        // Limpa tokens antigos caso ele tenha clicado várias vezes
        tokenRepository.deleteByUsuario(usuario);

        // Gera um token seguro e salva no banco (Validade: 1 hora)
        String tokenUuid = UUID.randomUUID().toString();
        RedefinicaoSenhaToken token = new RedefinicaoSenhaToken(
                tokenUuid,
                usuario,
                LocalDateTime.now().plusHours(1)
        );
        tokenRepository.save(token);

        // Dispara o e-mail
        String link = frontendUrl + "/redefinir-senha?token=" + tokenUuid;
        emailService.enviarEmailRecuperacao(usuario.getEmail(), usuario.getNome(), link);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        // Valida se o token existe e não expirou
        RedefinicaoSenhaToken redefinirToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RegraNegocioException("Token de recuperação inválido."));

        if (redefinirToken.isExpirado()) {
            tokenRepository.delete(redefinirToken);
            throw new RegraNegocioException("Este link de recuperação expirou. Solicite um novo.");
        }

        // Atualiza a senha no banco (criptografada)
        Usuario usuario = redefinirToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Deleta o token para que o link não possa ser reusado
        tokenRepository.delete(redefinirToken);
    }
}
