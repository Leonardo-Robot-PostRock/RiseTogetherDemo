package com.ITJobsBackend.authentication.application.ports.out;

public interface EmailSenderPort {
  void sendEmail(String to, String subject, String body);
}
