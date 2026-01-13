package com.eduverse.eduversebe.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${eduverse.mail.from-email}")
    private String fromEmail;

    @Value("${eduverse.mail.from-name}")
    private String fromName;

    @Async
    public void sendOtpEmail(String toEmail, String name, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);

            helper.setTo(toEmail);
            helper.setSubject("Your verification OTP for EduVerse");

            String content = String.format(
                    """
                    <p>Hello <b>%s</b>,</p>
                    <p>Your OTP is: <b style="color: blue; font-size: 18px;">%s</b></p>
                    <p>Valid for 10 minutes.</p>
                    """,
                    name, otp
            );

            helper.setText(content, true);

            mailSender.send(message);
            log.info("OTP sent to email: {}", toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }

    @Async
    public void sendForgotPasswordEmail(String toEmail, String userName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP");

            String htmlContent = String.format(
                    "Hello %s,<br><br>" +
                            "Your account recovery OTP is: <b>%s</b><br>" +
                            "This code is valid for 10 minutes.<br><br>" +
                            "Thank you!",
                    userName, otp
            );

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendVerificationEmail(String toEmail, String userName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Verify Email");

            String htmlContent = String.format(
                    "Hello %s,<br><br>" +
                            "Your email verification OTP is: <b>%s</b><br>" +
                            "This code is valid for 10 minutes.<br><br>" +
                            "Thank you!",
                    userName, otp
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
