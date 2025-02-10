package com.example.cleonoraadmin.validators.category;

import com.example.cleonoraadmin.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;

@RequiredArgsConstructor
public class UniqueCategoryNameValidator implements ConstraintValidator<UniqueCategoryName, Object> {

    private final CategoryRepository categoryRepository;
    private String idField;
    private String nameField;

    @Override
    public void initialize(UniqueCategoryName constraintAnnotation) {
        this.idField = constraintAnnotation.id();
        this.nameField = constraintAnnotation.name();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        
        String name = (String) beanWrapper.getPropertyValue(nameField);
        if (name == null) {
            return true;
        }

        Long id = (Long) beanWrapper.getPropertyValue(idField);
        
        boolean isValid = id != null ? 
            !categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id) : 
            !categoryRepository.existsByNameIgnoreCase(name);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode(nameField)
                   .addConstraintViolation();
        }
        
        return isValid;
    }
}
