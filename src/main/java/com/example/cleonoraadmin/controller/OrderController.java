package com.example.cleonoraadmin.controller;


import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderStatus;
import com.example.cleonoraadmin.model.category.CategoryResponse;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.model.order.OrderCleaningRequest;
import com.example.cleonoraadmin.service.CustomerService;
import com.example.cleonoraadmin.service.OrderCleaningService;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/order")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "order");
        model.addAttribute("title", "Заказы");
    }

    @GetMapping({"", "/"})
    public ModelAndView getOrderPage() {
        return new ModelAndView("order/orderPage");
    }

    @GetMapping("/getAllOrders")
    public @ResponseBody Page<OrderResponse> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "") String search,
                                                          @RequestParam(defaultValue = "5") Integer size) {
        return orderService.getPageAllOrders(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            OrderResponse orderResponse = orderService.getOrderById(id);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            log.error("Ошибка при получении заказа: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }


    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            boolean deleted = orderService.deleteOrder(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении заказа: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @PostMapping({"/create"})
    public @ResponseBody ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse createdOrder = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(createdOrder);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при создании заказа: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение целостности данных.");
        } catch (Exception e) {
            log.error("Ошибка при создании заказа: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @PutMapping({"/update"})
    public @ResponseBody ResponseEntity<?> updateOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse createdOrder = orderService.updateOrder(orderRequest);
            return ResponseEntity.ok(createdOrder);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при обновлении заказа: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение целостности данных.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении заказа: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @GetMapping("/status")
    public @ResponseBody ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(OrderStatus.values());
    }

}