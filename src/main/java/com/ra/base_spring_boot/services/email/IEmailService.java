package com.ra.base_spring_boot.services.email;

public interface IEmailService {
    void sendPasswordResetEmail(String toEmail, String userName, String otp);
    void sendPasswordChangedConfirmationEmail(String toEmail, String userName);
}
