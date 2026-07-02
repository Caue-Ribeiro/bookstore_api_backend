package com.caue.bookstore.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AppConfig {
    Dotenv dotenv = Dotenv.load();

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(dotenv.get("MAILSENDER_HOST"));
        mailSender.setPort(Integer.parseInt(dotenv.get("MAILSENDER_PORT")));

        mailSender.setUsername(dotenv.get("MAILSENDER_USERNAME"));
        mailSender.setPassword(dotenv.get("MAILSENDER_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");

        return mailSender;
    }
}
