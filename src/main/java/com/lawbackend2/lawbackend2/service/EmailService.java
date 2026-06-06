package com.lawbackend2.lawbackend2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@lawbackend.com}")
    private String fromEmail;

    @Value("${audit.notification.emails:}")
    private String notificationEmails;

    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("发送邮件失败: to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败: to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }

    public void sendAuditReportToManagers(String subject, String htmlContent) {
        if (notificationEmails == null || notificationEmails.trim().isEmpty()) {
            log.warn("未配置审计日志通知邮箱，跳过发送");
            return;
        }

        String[] emailList = notificationEmails.split("[,;]");
        for (String email : emailList) {
            email = email.trim();
            if (!email.isEmpty()) {
                sendHtmlEmail(email, subject, htmlContent);
            }
        }
    }
}
