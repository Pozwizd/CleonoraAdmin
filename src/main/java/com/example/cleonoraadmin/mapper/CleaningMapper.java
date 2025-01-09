package com.example.cleonoraadmin.mapper;

import com.example.cleonoraadmin.entity.Category;
import com.example.cleonoraadmin.entity.Cleaning;
import com.example.cleonoraadmin.entity.CleaningSpecifications;
import com.example.cleonoraadmin.model.cleaning.CleaningRequest;
import com.example.cleonoraadmin.model.cleaning.CleaningResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CategoryMapper.class, CleaningSpecificationsMapper.class}
)
public interface CleaningMapper {

    Cleaning toEntity(CleaningRequest cleaningRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Cleaning partialUpdate(CleaningRequest cleaningRequest, @MappingTarget Cleaning cleaning);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "cleaningSpecifications", target = "serviceSpecificationsId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "cleaningSpecifications.name", target = "serviceSpecificationsName")
    CleaningResponse toResponse(Cleaning cleaning);

    List<CleaningResponse> toResponseList(List<Cleaning> cleanings);

    default Page<CleaningResponse> toResponsePage(Page<Cleaning> servicesPage) {
        return servicesPage.map(this::toResponse);
    }

    // Маппинг для поля Category
    default Long toCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }

    // Маппинг для поля ServiceSpecifications
    default Long toServiceSpecificationsId(CleaningSpecifications cleaningSpecifications) {
        return cleaningSpecifications != null ? cleaningSpecifications.getId() : null;
    }
}
