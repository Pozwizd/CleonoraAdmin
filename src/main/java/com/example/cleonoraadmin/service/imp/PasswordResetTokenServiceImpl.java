package com.example.cleonoraadmin.service.imp;


import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.entity.PasswordResetToken;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.repository.PasswordResetTokenRepository;
import com.example.cleonoraadmin.service.PasswordResetTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updatePassword(String token, String password) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(()-> new EntityNotFoundException("Password reset token was not found by token "+token));
        passwordResetToken.getAdminUser().setPassword(passwordEncoder.encode(password));
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        boolean isValid = passwordResetToken.isPresent() && !passwordResetToken.get().getExpirationDate().isBefore(LocalDateTime.now());
        return isValid;
    }


    @Override
    public String createAndSavePasswordResetToken(AdminUser admin) {
        String token = UUID.randomUUID().toString();
        if(admin.getPasswordResetToken() != null){
            admin.getPasswordResetToken().setToken(token); // Existing token object is updated!
            admin.getPasswordResetToken().setExpirationDate();
            userRepository.save(admin);
        } else {
            PasswordResetToken passwordResetToken = new PasswordResetToken(token, admin);
            admin.setPasswordResetToken(passwordResetToken);
            passwordResetTokenRepository.save(passwordResetToken);

        }
        return token;
    }
}
