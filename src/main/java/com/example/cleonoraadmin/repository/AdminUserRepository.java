package com.example.cleonoraadmin.repository;

import com.example.cleonoraadmin.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long>, JpaSpecificationExecutor<AdminUser> {

    Optional<AdminUser> findByEmail(String email);

    List<AdminUser> findUserByEmail(String name);
}