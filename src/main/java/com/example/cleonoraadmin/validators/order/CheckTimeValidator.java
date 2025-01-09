package com.example.cleonoraadmin.validators.order;


import com.example.cleonoraadmin.entity.Workday;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.repository.WorkdayRepository;
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
public class CheckTimeValidator implements ConstraintValidator<CheckTime, Object> {

    private final WorkdayRepository workdayRepository;

    private String startTimeField;
    private String startDateField;

    @Override
    public void initialize(CheckTime constraintAnnotation) {
        this.startTimeField = constraintAnnotation.startTime();
        this.startDateField = constraintAnnotation.startDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        LocalTime startTime = (LocalTime) beanWrapper.getPropertyValue(startTimeField);
        LocalDate startDate = (LocalDate) beanWrapper.getPropertyValue(startDateField);

        if (startTime == null || startDate == null) {
            return true;
        }

        Workday workday = workdayRepository.findByDate(startDate).orElse(null);

        if (workday == null) {
            return true;
        }

        boolean hasConflict = workday.getTimeSlots().stream()
                .anyMatch(timeSlot -> {
                    LocalTime slotStartTime = timeSlot.getStartTime();
                    LocalTime slotEndTime = timeSlot.getEndTime();
                    return !startTime.isBefore(slotStartTime) && startTime.isBefore(slotEndTime);
                });

        if (hasConflict) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(startTimeField)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}