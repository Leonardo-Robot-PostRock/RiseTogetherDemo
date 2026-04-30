package com.risetogether.authentication.infrastructure.adapters.out.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;

import com.risetogether.authentication.application.ports.out.EmailSenderPort;

@Repository
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class SmtpEmailSenderAdapter implements EmailSenderPort {
  private static final Logger log = LoggerFactory.getLogger(SmtpEmailSenderAdapter.class);

  private final JavaMailSender mailSender;

  public SmtpEmailSenderAdapter(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendEmail(String to, String subject, String body) {
    log.info("Sending email to: {} with subject: {}", to, subject);
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
      log.info("Email sent successfully to: {}", to);
    } catch (Exception e) {
      log.error("Failed to send email to: {}", to, e);
      throw e;
    }
  }
}
