package org.example.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.auth.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    private void sendMail(String to, String subject, String content){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);

        javaMailSender.send(mailMessage);
    }

    @Override
    public void sendRegistrationEmail(String email, String username, String verificationToken) {
        String link = "http://localhost:8888/api/v1/auth/verify?token=" + verificationToken;
        String subject = "Confirm Your Registration";
        String content = """
        Hello %s,
        
        Please click the link below to confirm your email address:
        %s

        If you did not initiate this registration, please ignore this email.
        """.formatted(username, link);

        sendMail(email, subject, content);
    }
}
