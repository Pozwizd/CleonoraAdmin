package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.model.SalesChartDataDTO;
import com.example.cleonoraadmin.model.TopCleaningDTO;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.service.CustomerService;
import com.example.cleonoraadmin.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("/orders/total-sales")
    @ResponseBody
    public BigDecimal getTotalSales() {
        return orderService.calculateTotalSales();
    }

    @GetMapping("/customers/active")
    @ResponseBody
    public Integer getActiveCustomers() {
        return customerService.countActiveCustomers(30);
    }

    @GetMapping("/customers/active/changes")
    @ResponseBody
    public Integer getActiveCustomersChanges() {
        return customerService.countActiveCustomers(30);
    }

    @GetMapping("/orders/sales-last-week")
    @ResponseBody
    public BigDecimal getSalesLastWeek() {
        return orderService.calculateSalesLastWeek();
    }

    @GetMapping("/order/getAllOrdersLastWeek")
    public @ResponseBody Page<OrderResponse> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "") String search,
                                                          @RequestParam(defaultValue = "5") Integer size) {
        return orderService.getPageAllOrdersLastWeek(0, 5, search);
    }

}



