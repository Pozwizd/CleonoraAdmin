package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.service.AdminUserService;
import com.example.cleonoraadmin.service.MailService;
import com.example.cleonoraadmin.service.PasswordResetTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class LoginController {
    private final AdminUserService adminUserService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final MailService mailService;


    @GetMapping("/login")
    public ModelAndView login(Model model) {
        model.addAttribute("title", "Вход в систему");
        return new ModelAndView("login/login");
    }


    @GetMapping("/confirmation")
    public String confirmSending(){
        return "login/confirmation";
    }
    @GetMapping("/forgotPassword")
    public String forgotPassword(){
        return "login/forgotPassword";
    }

    @GetMapping("/changePassword")
    public String changePassword(@RequestParam("token")String token, Model model){
        model.addAttribute("token", token);
        if(passwordResetTokenService.validatePasswordResetToken(token)){
            return "login/changePassword";
        } else {
            return "login/expired";
        }
    }
    @GetMapping("/success")
    public String success(){
        return "login/successful";
    }
    @PostMapping("/changePassword")
    public @ResponseBody String setNewPassword(@RequestParam("token")String token, @RequestParam("password")String password){
        if(passwordResetTokenService.validatePasswordResetToken(token)){
            passwordResetTokenService.updatePassword(token,password);
            return "success";
        } else {
            return "wrong";
        }
    }
    @PostMapping("/resetPassword")
    public @ResponseBody String resetPassword(HttpServletRequest request, @RequestParam("email")String email){
        if(email.isEmpty()){
            return "blank";
        }
        Optional<AdminUser> admin = adminUserService.getUserByEmail(email);
        if(admin.isEmpty()){
            return "wrong";
        }
        String token = passwordResetTokenService.createAndSavePasswordResetToken(admin.get());
        mailService.sendToken(token,email,request);
        return "success";
    }

}