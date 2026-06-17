package br.com.mercadoprodutor.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailRecuperacao(String para, String nome, String link) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(para);
        mensagem.setSubject("Recuperação de Senha - Mercado do Produtor");
        mensagem.setText("Olá, " + nome + "!\n\n" +
                "Recebemos uma solicitação para redefinir a sua senha.\n" +
                "Acesse o link abaixo para criar uma nova senha (válido por 1 hora):\n\n" +
                link + "\n\n" +
                "Se você não solicitou isso, pode ignorar este e-mail.\n" +
                "Atenciosamente,\nEquipe Mercado do Produtor.");

        mailSender.send(mensagem);
    }
}