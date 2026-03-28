package br.edu.pe.senac.projeto_pi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCredenciais(String nome, String email, String senhaProvisoria) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Acesso ao Sistema Senac - Horas Complementares");

        message.setText(
            "Olá " + nome + ",\n\n" +
            "Sua conta foi criada no sistema.\n\n" +
            "Email: use seu e-mail para acesso "+email+"\n" +
            "IMPORTANTE - Senha provisória: " + senhaProvisoria + "\n\n" +
            "Recomendamos que você altere sua senha no primeiro acesso.\n\n" +
            "Atenciosamente,\n" +
            "Equipe Senac"
        );

        mailSender.send(message);
    }
}
