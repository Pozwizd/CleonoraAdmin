package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.Customer;
import com.example.cleonoraadmin.model.customer.CustomerRequest;
import com.example.cleonoraadmin.model.customer.CustomerResponse;
import com.example.cleonoraadmin.service.CustomerService;
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
@RequestMapping("/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "customer");
        model.addAttribute("title", "Клиенты");
    }

    @GetMapping({"", "/"})
    public ModelAndView getCustomerPage() {
        return new ModelAndView("customer/customerPage");
    }

    @GetMapping("/getAllCustomers")
    public @ResponseBody Page<CustomerResponse> getAllCustomers(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "") String search,
                                                                @RequestParam(defaultValue = "5") Integer size) {
        return customerService.getPageAllCustomers(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getCustomer(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerResponseById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        try {
            CustomerResponse createdCustomer = customerService.saveNewCustomer(customerRequest);
            return ResponseEntity.ok(createdCustomer);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при создании клиента: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Дублирующаяся запись. Клиент с таким email уже существует.");
        } catch (Exception e) {
            log.error("Ошибка при создании клиента: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest) {
        try {
            Optional<Customer> existingCustomer = customerService.getCustomerById(id);
            if (existingCustomer.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(customerService.updateCustomer(id, customerRequest));
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при обновлении клиента: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение уникальности данных.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении клиента: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            boolean deleted = customerService.deleteCustomerById(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении клиента: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }
}
