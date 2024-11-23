package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.repository.AdminUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice(basePackages = "com.example.cleonoraadmin.controller")
public class ControllerContext {

    private final AdminUserRepository userRepository;

    @Value("${spring.application.name}")
    private String appName;

    public ControllerContext(AdminUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("appName", appName);

    }


    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {



        if (principal != null) {
            model.addAttribute("userName", userRepository
                    .findByEmail(principal.getName()).get().getName());
            model.addAttribute("userRole", userRepository
                    .findByEmail(principal.getName()).get().getRole());
            String email = principal.getName();
            System.out.println(email);
        } else {
            System.out.println("No principal");
        }
    }
}
