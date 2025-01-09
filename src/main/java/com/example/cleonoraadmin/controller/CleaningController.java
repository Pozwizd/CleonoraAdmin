package com.example.cleonoraadmin.controller;

import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.model.cleaning.CleaningRequest;
import com.example.cleonoraadmin.model.cleaning.CleaningResponse;
import com.example.cleonoraadmin.service.CleaningService;
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
@RequestMapping("/cleaning")
@AllArgsConstructor
@Slf4j
public class CleaningController {

    private final CleaningService cleaningService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "cleaning");
        model.addAttribute("title", "Услуги");
    }



    @GetMapping({"", "/"})
    public ModelAndView getServicePage() {
        return new ModelAndView("cleaning/cleaningPage");
    }

    @GetMapping("/getAllServices")
    public @ResponseBody Page<CleaningResponse> getAllServices(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "") String search,
                                                               @RequestParam(defaultValue = "5") Integer size) {
        return cleaningService.getPageAllServices(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getService(@PathVariable Long id) {
        CleaningResponse service = cleaningService.getServiceResponseById(id);
        if (service != null) {
            return ResponseEntity.ok(service);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createService(@Valid @RequestBody CleaningRequest cleaningRequest) {
        try {
            CleaningResponse createdService = cleaningService.saveNewService(cleaningRequest);
            return ResponseEntity.ok(createdService);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при создании услуги: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Дублирующаяся запись. Услуга с таким именем уже существует.");
        } catch (Exception e) {
            log.error("Ошибка при создании услуги: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody CleaningRequest cleaningRequest) {
        try {
            Optional<Cleaning> existingService = cleaningService.getServiceById(id);
            if (existingService.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(cleaningService.updateService(id, cleaningRequest));
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка базы данных при обновлении услуги: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Ошибка: Нарушение уникальности данных.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении услуги: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            boolean deleted = cleaningService.deleteServiceById(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении услуги: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }
}
