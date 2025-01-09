package com.example.cleonoraadmin.controller;


import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsResponse;
import com.example.cleonoraadmin.service.CleaningSpecificationsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/specifications", "/specifications/"})
@AllArgsConstructor
@Slf4j
public class ServiceSpecificationsController {

    private final CleaningSpecificationsService cleaningSpecificationsService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("pageActive", "specifications");
        model.addAttribute("title", "Спецификации");
    }

    @GetMapping({"/", ""})
    public ModelAndView index() {
        return new ModelAndView("specifications/specificationCleaningPage");
    }


    @GetMapping("/getAllSpecificationsAdmin")
    public @ResponseBody List<ServiceSpecificationsResponse> getAllSpecificationsAdmin() {
        return cleaningSpecificationsService.getAllServiceSpecifications();
    }


    @GetMapping("/getAllSpecifications")
    public @ResponseBody Page<ServiceSpecificationsResponse> getAllSpecifications(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "") String search,
                                                                                  @RequestParam(defaultValue = "5") Integer size) {
        return cleaningSpecificationsService.getAllServiceSpecifications(page, size, search);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getSpecification(@PathVariable Long id) {
        ServiceSpecificationsResponse specification = cleaningSpecificationsService.getServiceSpecificationsResponseById(id);
        if (specification != null) {
            return ResponseEntity.ok(specification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping({"/create"})
    public ResponseEntity<?> createSpecification(@Valid @ModelAttribute ServiceSpecificationsRequest specificationRequest) {
        ServiceSpecificationsResponse createdSpecification = cleaningSpecificationsService.saveNewServiceSpecifications(specificationRequest);
        return ResponseEntity.ok(createdSpecification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpecification( @Valid @ModelAttribute ServiceSpecificationsRequest specificationRequest) {
        Optional<CleaningSpecifications> existingSpecification = cleaningSpecificationsService.getServiceSpecificationsById(specificationRequest.getId());
        if (existingSpecification.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cleaningSpecificationsService.updateServiceSpecifications(specificationRequest.getId(), specificationRequest));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpecification(@PathVariable Long id) {
        try {
            boolean deleted = cleaningSpecificationsService.deleteServiceSpecificationsById(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении спецификации: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Произошла непредвиденная ошибка.");
        }
    }
}
