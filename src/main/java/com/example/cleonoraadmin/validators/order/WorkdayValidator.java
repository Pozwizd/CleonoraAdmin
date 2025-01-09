package com.example.cleonoraadmin.validators.order;

import com.example.cleonoraadmin.config.WorkScheduleConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class WorkdayValidator implements ConstraintValidator<ValidWorkday, Object> {

    private final WorkScheduleConfig workScheduleConfig;

    private String startTimeField;
    private String startDateField;

    @Override
    public void initialize(ValidWorkday constraintAnnotation) {
        this.startTimeField = constraintAnnotation.startTime();
        this.startDateField = constraintAnnotation.startDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Если объект пустой, пропускаем валидацию
        }

        // Доступ к полям через BeanWrapper
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        LocalDate startDate = (LocalDate) beanWrapper.getPropertyValue(startDateField);
        LocalTime startTime = (LocalTime) beanWrapper.getPropertyValue(startTimeField);

        if (startDate == null || startTime == null) {
            return true; // Если дата или время отсутствуют, пропускаем валидацию
        }

        // Получаем название дня недели
        String dayOfWeek = startDate.getDayOfWeek().toString().toLowerCase();

        // Извлекаем расписание для указанного дня недели
        WorkScheduleConfig.WorkDay workDay = workScheduleConfig.getSchedule().get(dayOfWeek);

        if (workDay == null || "off".equalsIgnoreCase(workDay.getStart())) {
            // Если день выходной, дата считается невалидной
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Выбранный день является выходным")
                    .addPropertyNode(startDateField)
                    .addConstraintViolation();
            return false;
        }


        // Если всё корректно
        return true;
    }
}