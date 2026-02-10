package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.exceptions.UserNotFoundException;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendEmail(String email) {

        if (email == null) { throw new UserNotFoundException();}

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromMail);
        message.setTo(email);
        message.setSubject("Confirmação de Cadastro: Simple Bank");
        message.setText("Parabéns!! Seja Bem Vindo(a) ao serviços do Simple Bank sua Carteira Digital foi criada com Sucesso!");

        mailSender.send(message);
    }
}
