package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.entity.PasswordResetToken;
import com.example.cleonoraadmin.repository.AdminUserRepository;
import com.example.cleonoraadmin.repository.PasswordResetTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PasswordResetTokenServiceImplTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AdminUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;

    @Test
    void updatePassword_ValidToken_ShouldUpdatePasswordAndSaveToken() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";
        AdminUser adminUser = createAdminUser(1L, "user@example.com");
        PasswordResetToken passwordResetToken = createPasswordResetToken(1L, token, adminUser);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(passwordResetTokenRepository.save(passwordResetToken)).thenReturn(passwordResetToken);

        passwordResetTokenService.updatePassword(token, newPassword);

        assertEquals(encodedPassword, passwordResetToken.getAdminUser().getPassword());
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(passwordResetTokenRepository, times(1)).save(passwordResetToken);
    }

    @Test
    void updatePassword_InvalidToken_ShouldThrowEntityNotFoundException() {
        String token = UUID.randomUUID().toString();
        String newPassword = "newPassword";

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passwordResetTokenService.updatePassword(token, newPassword));

        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(passwordResetTokenRepository);
    }

    @Test
    void validatePasswordResetToken_ValidToken_ShouldReturnTrue() {
        String token = UUID.randomUUID().toString();
        AdminUser adminUser = createAdminUser(1L, "user@example.com");
        PasswordResetToken passwordResetToken = createPasswordResetToken(1L, token, adminUser);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusHours(1)); // Set expiration in the future

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        boolean isValid = passwordResetTokenService.validatePasswordResetToken(token);

        assertTrue(isValid);
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void validatePasswordResetToken_InvalidToken_TokenNotFound_ShouldReturnFalse() {
        String token = UUID.randomUUID().toString();

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        boolean isValid = passwordResetTokenService.validatePasswordResetToken(token);

        assertFalse(isValid);
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void validatePasswordResetToken_InvalidToken_TokenExpired_ShouldReturnFalse() {
        String token = UUID.randomUUID().toString();
        AdminUser adminUser = createAdminUser(1L, "user@example.com");
        PasswordResetToken passwordResetToken = createPasswordResetToken(1L, token, adminUser);
        passwordResetToken.setExpirationDate(LocalDateTime.now().minusHours(1)); // Set expiration in the past

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        boolean isValid = passwordResetTokenService.validatePasswordResetToken(token);

        assertFalse(isValid);
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void createAndSavePasswordResetToken_UpdateToken_ShouldUpdateExistingTokenAndSaveUser() {
        AdminUser adminUser = createAdminUser(1L, "user@example.com");
        PasswordResetToken existingToken = createPasswordResetToken(1L, UUID.randomUUID().toString(), adminUser);
        adminUser.setPasswordResetToken(existingToken); 

        String originalTokenValue = existingToken.getToken();

        when(userRepository.save(Mockito.any(AdminUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String newToken = passwordResetTokenService.createAndSavePasswordResetToken(adminUser);

        assertNotNull(newToken);
        assertNotEquals(originalTokenValue, adminUser.getPasswordResetToken().getToken()); 
        assertEquals(newToken, adminUser.getPasswordResetToken().getToken());
        verify(userRepository, times(1)).save(adminUser);
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void createAndSavePasswordResetToken_NewAdminUser_ShouldCreateNewTokenAndSaveTokenRepository() {
        AdminUser adminUser = createAdminUser(1L, "user@example.com");
        adminUser.setPasswordResetToken(null); 

        when(passwordResetTokenRepository.save(Mockito.any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = passwordResetTokenService.createAndSavePasswordResetToken(adminUser);

        assertNotNull(token);
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class)); 
        verify(userRepository, never()).save(any(AdminUser.class)); 
        assertNotNull(adminUser.getPasswordResetToken()); 
        assertEquals(token, adminUser.getPasswordResetToken().getToken()); 
    }



    private AdminUser createAdminUser(Long id, String email) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(id);
        adminUser.setEmail(email);
        adminUser.setPassword("password");
        return adminUser;
    }

    private PasswordResetToken createPasswordResetToken(Long id, String token, AdminUser adminUser) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, adminUser);
        passwordResetToken.setId(id);
        return passwordResetToken;
    }
}