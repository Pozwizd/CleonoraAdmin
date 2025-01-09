package com.example.cleonoraadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 AdminUser
- id: Long
- name: String
- surname: String
- avatar: String
- email: String
- phoneNumber: String
- password: String
- isActive: Boolean
- role: AdminRole
- passwordResetToken: PasswordResetToken
--
+ getId(): Long
+ setId(Long id)
+ getName(): String
+ setName(String name)
+ getSurname(): String
+ setSurname(String surname)
+ getAvatar(): String
+ setAvatar(String avatar)
+ getEmail(): String
+ setEmail(String email)
+ getPhoneNumber(): String
+ setPhoneNumber(String phoneNumber)
+ getPassword(): String
+ setPassword(String password)
+ getIsActive(): Boolean
+ setIsActive(Boolean isActive)
+ getRole(): AdminRole
+ setRole(AdminRole role)
+ getPasswordResetToken(): PasswordResetToken
+ setPasswordResetToken(PasswordResetToken passwordResetToken)
 */

@Entity
@Data
public class AdminUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String avatar;

    private String email;

    private String phoneNumber;

    private String password;

    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private AdminRole role = AdminRole.ADMIN;

    @OneToOne(mappedBy = "adminUser", cascade = CascadeType.ALL)
    private PasswordResetToken passwordResetToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

}
