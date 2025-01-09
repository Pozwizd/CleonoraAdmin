package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
AdminUserRepository
+ findByEmail(String email): Optional<AdminUser>
+ findUserByEmail(String name): List<AdminUser>
+ findByPhoneNumber(String phoneNumber): Optional<AdminUser>
 */

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long>, JpaSpecificationExecutor<AdminUser> {

    Optional<AdminUser> findByEmail(String email);

    List<AdminUser> findUserByEmail(String name);

    Optional<AdminUser> findByPhoneNumber(String phoneNumber);
}