package com.example.cleonoraadmin.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.example.cleonoraadmin.controller")
public class ControllerContext {

    @Value("${spring.application.name}")
    private String appName;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("appName", appName);

    }
}
