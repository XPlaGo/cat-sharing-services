package ru.xplago.mailservice.services;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private ResourceLoader resourceLoader;
    private final JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender, ResourceLoader resourceLoader) {
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }

    public void sendVerificationCode(String email, String verificationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("security@catsharing.ru");
        message.setRecipients(Message.RecipientType.TO, email);
        message.setSubject("Email verification");

        try {
            Map<String, String> model = new HashMap<>();
            model.put("verificationCode", verificationCode);
            String html = replaceWithTemplate(readTemplate("classpath:templates/verification.html"), model);
            message.setContent(html, "text/html");

        } catch (IOException e) {
            message.setContent(String.format("Verification code: %s", verificationCode), "text/plain");
        }
        mailSender.send(message);
    }

    public String readTemplate(String location) throws IOException {
        Resource resource = resourceLoader.getResource(location);
        return Files.readString(Paths.get(resource.getURI()));
    }

    public String replaceWithTemplate(String template, Map<String, String> model) {
        for (Map.Entry<String, String> entry : model.entrySet()) {
            template = template.replace(
                    String.format("{%s}", entry.getKey()),
                    entry.getValue()
            );
        }
        return template;
    }
}
