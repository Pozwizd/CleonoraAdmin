package com.example.cleonoraadmin.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                model.addAttribute("textError", "Страница отсутствует");
                return new ModelAndView("error/error");
            } else if (statusCode == 500) {
                model.addAttribute("textError", "Произошла ошибка");
                return new ModelAndView("error/error");
            } else if (statusCode == 403) {
                model.addAttribute("textError", "Доступ запрещен");
                return new ModelAndView("error/error");
            } else if (statusCode == 401) {
                model.addAttribute("textError", "Вы не авторизованы");
                return new ModelAndView("error/error");
            } else if (statusCode == 405) {
                model.addAttribute("textError", "Метод не поддерживается");
                return new ModelAndView("error/error");
            }
        }
        model.addAttribute("textError", "Произошла ошибка");
        return new ModelAndView("error/error");
    }
}
