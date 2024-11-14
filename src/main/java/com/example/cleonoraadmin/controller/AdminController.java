package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.AdminUser;
import com.example.cleonoraadmin.model.AdminUserResponse;
import com.example.cleonoraadmin.service.AdminUserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping({"", "/"})
    public ModelAndView admin() {
        return new ModelAndView("user/usersPage");
    }

    @GetMapping("/getAll")
    @ResponseBody
    public Page<AdminUserResponse> getAllAdminUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "") String search,
                                       @RequestParam(defaultValue = "5") Integer size) {

        return adminUserService.getPageAllAdminUsers(page, size, search);
    }


}
