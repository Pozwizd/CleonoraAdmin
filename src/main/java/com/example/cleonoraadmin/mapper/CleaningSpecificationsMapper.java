package com.example.cleonoraadmin.mapper;

import com.example.cleonoraadmin.UploadFile;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleonoraadmin.model.serviceSpecifications.ServiceSpecificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CleaningSpecificationsMapper {



    ServiceSpecificationsResponse toResponse (CleaningSpecifications cleaningSpecifications);

    default CleaningSpecifications toEntity(ServiceSpecificationsRequest request, UploadFile uploadFile) {
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(request.getId());
        cleaningSpecifications.setName(request.getName());
        cleaningSpecifications.setBaseCost(request.getBaseCost());
        cleaningSpecifications.setComplexityCoefficient(request.getComplexityCoefficient());
        cleaningSpecifications.setEcoFriendlyExtraCost(request.getEcoFriendlyExtraCost());
        cleaningSpecifications.setFrequencyCoefficient(request.getFrequencyCoefficient());
        cleaningSpecifications.setLocationCoefficient(request.getLocationCoefficient());
        cleaningSpecifications.setTimeMultiplier(request.getTimeMultiplier());
        cleaningSpecifications.setUnit(request.getUnit());
        if (request.getIcon() != null && !request.getIcon().isEmpty()) {
            String iconPath = uploadFile.uploadFile(request.getIcon(), null);
            cleaningSpecifications.setIcon(iconPath);
        }
        return cleaningSpecifications;
    }


    default Page<ServiceSpecificationsResponse> toResponsePage(Page<CleaningSpecifications> serviceSpecificationsPage) {
        return serviceSpecificationsPage.map(this::toResponse);
    }

}