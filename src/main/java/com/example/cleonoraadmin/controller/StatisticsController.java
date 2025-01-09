package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.model.SalesChartDataDTO;
import com.example.cleonoraadmin.model.TopCleaning;
import com.example.cleonoraadmin.model.TopCleaningDTO;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.service.CustomerService;
import com.example.cleonoraadmin.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/"})
@AllArgsConstructor
public class StatisticsController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "statistic");
        model.addAttribute("title", "Статистика");
    }

    @GetMapping({"/", ""})
    public ModelAndView index() {
        return new ModelAndView("statistic");
    }

    @GetMapping("/customers")
    @ResponseBody
    public Integer getAllCustomers() {
        return customerService.countAllCustomers();
    }

    @GetMapping("/customers/daily")
    @ResponseBody
    public Integer getDailyNewCustomers() {
        return customerService.countDailyNewCustomers();
    }

    @GetMapping("/orders/statistic")
    @ResponseBody
    public Integer getAllCountOrders() {
        return orderService.countCompletedOrders();
    }

    @GetMapping("/orders/today")
    @ResponseBody
    public Integer getTodayCountOrders() {
        return orderService.countDailyCompletedOrders();
    }


    @GetMapping("/chart")
    @ResponseBody
    public SalesChartDataDTO getSalesChart(@RequestParam(name = "quantity") int quantity,
                                           @RequestParam(name = "months") int months) {
        return orderService.getSalesChartData(quantity, months);
    }


    @GetMapping("/top")
    @ResponseBody
    public List<TopCleaningDTO> getTopCleanings(@RequestParam(name = "topN") int topN,
                                                @RequestParam(name = "months") int months) {
        return orderService.getTopCleanings(topN, months);
    }


}



