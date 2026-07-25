package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                    <div style="background-color: #2c3e50; padding: 20px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0;">Inventory ERP</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #2c3e50;">Welcome, %s!</h2>
                        <p style="color: #555555; font-size: 15px; line-height: 1.6;">
                            Your account has been registered successfully on Inventory ERP.
                        </p>
                    </div>
                </div>
                """.formatted(username);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Inventory ERP!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}