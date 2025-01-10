package com.example.cleonoraadmin.config;

import com.example.cleonoraadmin.entity.*;
import com.example.cleonoraadmin.model.order.OrderCleaningRequest;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.service.*;
import com.example.cleonoraadmin.service.CleaningService;
import com.example.cleonoraadmin.service.OrderService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataLoader {
    private final Faker faker;
    private final AdminUserService adminUserService;
    private final CategoryService categoryService;
    private final CleaningSpecificationsService cleaningSpecificationsService;
    private final CustomerService customerService;
    private final CleaningService cleaningService;
    private final OrderService orderService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadUsers() {
        if (adminUserService.findByUsername("admin@gmail.com") == null) {
            AdminUser adminUser = new AdminUser();
            adminUser.setName("Admin");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPhoneNumber(faker.phoneNumber().cellPhone());
            adminUser.setPassword("admin");
            adminUserService.saveNewUser(adminUser);
        }

        while (!adminUserService.ifUserMoreThan(100)) {
            AdminUser adminUser = new AdminUser();
            adminUser.setName(faker.name().fullName());
            adminUser.setSurname(faker.name().lastName());
            adminUser.setEmail(faker.internet().emailAddress());
            adminUser.setPhoneNumber(faker.phoneNumber().cellPhone());
            adminUser.setPassword(faker.internet().password());
            adminUser.setRole(AdminRole.MANAGER);
            adminUserService.saveNewUser(adminUser);
        }

        while (!categoryService.ifCategoryMoreThan(50)) {
            Category category = new Category();
            category.setName(faker.commerce().productName());
            category.setDescription(faker.lorem().sentence());
            categoryService.save(category);
        }

        if (!cleaningSpecificationsService.ifServiceSpecificationsMoreThan(5)) {
            loadServiceSpecifications();
        }

        while (!customerService.ifCustomerCountMoreThan(50)) {
            Customer customer = new Customer();
            customer.setName(faker.name().fullName());
            customer.setEmail(faker.internet().emailAddress());

            customer.setPhoneNumber(faker.phoneNumber().cellPhone());
            customer.setPassword(faker.internet().password());
            customerService.save(customer);

        }

        while (!cleaningService.ifServiceMoreThan(50)) {
            Cleaning cleaning = new Cleaning();
            cleaning.setName(faker.commerce().productName());
            cleaning.setDescription(faker.lorem().sentence());
            cleaning.setCategory(categoryService.getCategoryById((long) (Math.random() * 25) + 1).get());

            cleaning.setCleaningSpecifications(cleaningSpecificationsService.getServiceSpecificationsById(
                    (long) (Math.random() * 11) + 1).get());
            cleaningService.save(cleaning);
        }

        LocalDate startDate = LocalDate.parse("2025-01-01");
        while (!orderService.ifOrderMoreThan(5)) {
            startDate = startDate.plusDays(5);
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setStartDate(startDate);
            orderRequest.setStartTime(LocalTime.parse("10:30"));
            orderRequest.setCustomerId(1L);
            orderRequest.setStatus(OrderStatus.COMPLETED);
            OrderCleaningRequest orderCleaningRequest = new OrderCleaningRequest();
            orderCleaningRequest.setCleaningId(1L);
            orderCleaningRequest.setNumberUnits(5);
            orderRequest.setOrderCleanings(new ArrayList<>());
            orderRequest.getOrderCleanings().add(orderCleaningRequest);
            orderService.createOrder(orderRequest);
        }


    }

    private void loadServiceSpecifications() {
        CleaningSpecifications spec1 = new CleaningSpecifications();
        spec1.setName("Глубокая очистка квартиры (до 50 м²)");
        spec1.setBaseCost(10.0);
        spec1.setComplexityCoefficient(1.5);
        spec1.setFrequencyCoefficient(1.0);
        spec1.setLocationCoefficient(1.1);
        spec1.setEcoFriendlyExtraCost(10.0);
        spec1.setTimeMultiplier(0.2);
        spec1.setUnit("м²");
        spec1.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec1);

        CleaningSpecifications spec2 = new CleaningSpecifications();
        spec2.setName("Генеральная уборка дома (до 150 м²)");
        spec2.setBaseCost(8.0);
        spec2.setComplexityCoefficient(1.5);
        spec2.setFrequencyCoefficient(1.0);
        spec2.setLocationCoefficient(1.1);
        spec2.setEcoFriendlyExtraCost(10.0);
        spec2.setTimeMultiplier(0.15);
        spec2.setUnit("м²");
        spec2.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec2);

        CleaningSpecifications spec3 = new CleaningSpecifications();
        spec3.setName("Еженедельная уборка квартиры (до 50 м²)");
        spec3.setBaseCost(7.0);
        spec3.setComplexityCoefficient(1.0);
        spec3.setFrequencyCoefficient(0.9);
        spec3.setLocationCoefficient(1.0);
        spec3.setEcoFriendlyExtraCost(0.0);
        spec3.setTimeMultiplier(0.1);
        spec3.setUnit("м²");
        spec3.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec3);

        CleaningSpecifications spec4 = new CleaningSpecifications();
        spec4.setName("Ежемесячная уборка офиса (до 100 м²)");
        spec4.setBaseCost(6.0);
        spec4.setComplexityCoefficient(1.0);
        spec4.setFrequencyCoefficient(0.95);
        spec4.setLocationCoefficient(1.1);
        spec4.setEcoFriendlyExtraCost(0.0);
        spec4.setTimeMultiplier(0.1);
        spec4.setUnit("м²");
        spec4.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec4);

        CleaningSpecifications spec5 = new CleaningSpecifications();
        spec5.setName("Очистка после косметического ремонта (до 50 м²)");
        spec5.setBaseCost(15.0);
        spec5.setComplexityCoefficient(2.0);
        spec5.setFrequencyCoefficient(1.0);
        spec5.setLocationCoefficient(1.1);
        spec5.setEcoFriendlyExtraCost(10.0);
        spec5.setTimeMultiplier(0.3);
        spec5.setUnit("м²");
        spec5.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec5);

        CleaningSpecifications spec6 = new CleaningSpecifications();
        spec6.setName("Уборка после капитального ремонта (до 150 м²)");
        spec6.setBaseCost(20.0);
        spec6.setComplexityCoefficient(2.0);
        spec6.setFrequencyCoefficient(1.0);
        spec6.setLocationCoefficient(1.1);
        spec6.setEcoFriendlyExtraCost(10.0);
        spec6.setTimeMultiplier(0.25);
        spec6.setUnit("м²");
        spec6.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec6);

        CleaningSpecifications spec7 = new CleaningSpecifications();
        spec7.setName("Химчистка дивана");
        spec7.setBaseCost(50.0);
        spec7.setComplexityCoefficient(1.0);
        spec7.setFrequencyCoefficient(1.0);
        spec7.setLocationCoefficient(1.0);
        spec7.setEcoFriendlyExtraCost(0.0);
        spec7.setTimeMultiplier(1.5);
        spec7.setUnit("pcs");
        spec7.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec7);

        CleaningSpecifications spec8 = new CleaningSpecifications();
        spec8.setName("Химчистка ковра (до 20 м²)");
        spec8.setBaseCost(4.0);
        spec8.setComplexityCoefficient(1.0);
        spec8.setFrequencyCoefficient(1.0);
        spec8.setLocationCoefficient(1.0);
        spec8.setEcoFriendlyExtraCost(0.0);
        spec8.setTimeMultiplier(0.05);
        spec8.setUnit("м²");
        spec8.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec8);

        CleaningSpecifications spec9 = new CleaningSpecifications();
        spec9.setName("Уборка после вечеринки (до 50 м²)");
        spec9.setBaseCost(25.0);
        spec9.setComplexityCoefficient(1.2);
        spec9.setFrequencyCoefficient(1.0);
        spec9.setLocationCoefficient(1.0);
        spec9.setEcoFriendlyExtraCost(0.0);
        spec9.setTimeMultiplier(0.3);
        spec9.setUnit("м²");
        spec9.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec9);

        CleaningSpecifications spec10 = new CleaningSpecifications();
        spec10.setName("Уборка после корпоратива (до 100 м²)");
        spec10.setBaseCost(30.0);
        spec10.setComplexityCoefficient(1.2);
        spec10.setFrequencyCoefficient(1.0);
        spec10.setLocationCoefficient(1.1);
        spec10.setEcoFriendlyExtraCost(0.0);
        spec10.setTimeMultiplier(0.35);
        spec10.setUnit("м²");
        spec10.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec10);

        CleaningSpecifications spec11 = new CleaningSpecifications();
        spec11.setName("Химчистка кресла");
        spec11.setBaseCost(30.0);
        spec11.setComplexityCoefficient(1.0);
        spec11.setFrequencyCoefficient(1.0);
        spec11.setLocationCoefficient(1.0);
        spec11.setEcoFriendlyExtraCost(0.0);
        spec11.setTimeMultiplier(1.2);
        spec11.setUnit("pcs");
        spec11.setIcon("/path/measurement_unit_icon");
        cleaningSpecificationsService.save(spec11);
    }
}
