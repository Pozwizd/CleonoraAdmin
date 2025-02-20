package com.example.cleonoraadmin.validators.order;

import com.example.cleonoraadmin.entity.AddressOrder;
import com.example.cleonoraadmin.exception.GeoapifyProcessingException;
import com.example.cleonoraadmin.model.order.CustomerAddressRequest;
import com.example.cleonoraadmin.service.GeoapifyService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;



public class LatLonOrAddressValidator implements ConstraintValidator<LatLonOrAddress, CustomerAddressRequest> {

    private final GeoapifyService geoapifyService;

    public LatLonOrAddressValidator(GeoapifyService geoapifyService) {
        this.geoapifyService = geoapifyService;
    }

    @Override
    public boolean isValid(CustomerAddressRequest address, ConstraintValidatorContext context) {
        boolean hasCoordinates = address.getLat() != null && address.getLon() != null;
        boolean hasAddress = address.getCity() != null && !address.getCity().isEmpty()
                && address.getStreet() != null && !address.getStreet().isEmpty()
                && address.getHouseNumber() != null && !address.getHouseNumber().isEmpty();

        if (!hasCoordinates && !hasAddress) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Необходимо указать корректные координаты или адрес.")
                    .addConstraintViolation();
            return false;
        }

        try {
            AddressOrder result = geoapifyService.processAddress(address);
            return result != null;
        } catch (GeoapifyProcessingException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }
}