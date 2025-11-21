package com.ra.base_spring_boot.services.auth;


import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.email.IEmailService;
import com.ra.base_spring_boot.repository.user.IPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements IPasswordResetService {
    private final IUserRepository userRepository;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final IEmailService emailService;


}
