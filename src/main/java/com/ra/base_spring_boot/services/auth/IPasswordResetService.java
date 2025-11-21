package com.ra.base_spring_boot.services.auth;


import com.ra.base_spring_boot.dto.auth.ForgotPasswordRequest;
import com.ra.base_spring_boot.dto.auth.ResetPasswordRequest;
import com.ra.base_spring_boot.dto.auth.VerifyOtpRequest;

public interface IPasswordResetService {
    void requestPasswordReset(ForgotPasswordRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
    void cleanupExpiredTokens();
}
