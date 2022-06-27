package com.example.formproject.service;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    private final JwtProvider provider;

    public String sendHtmlEmail(MailDto dto,String randomChars) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(FinalValue.APPLICATION_TITLE);
        helper.setTo(dto.getEmail());
        message.setContent(dto.buildContent(randomChars), "text/html; charset=utf-8");
        mailSender.send(message);
        return provider.generateToken(randomChars);
    }
}
