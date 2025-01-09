package com.example.cleonoraadmin.mapper;

import com.example.cleonoraadmin.entity.Order;
import com.example.cleonoraadmin.entity.OrderCleaning;
import com.example.cleonoraadmin.model.order.OrderCleaningRequest;
import com.example.cleonoraadmin.model.order.OrderRequest;
import com.example.cleonoraadmin.model.order.OrderResponse;
import com.example.cleonoraadmin.repository.CustomerRepository;
import com.example.cleonoraadmin.repository.CleaningRepository;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderCleaningMapper.class,
        CustomerRepository.class, CleaningRepository.class})
public interface OrderMapper {



    default Order toEntity(OrderRequest orderRequest,
                           @Context CustomerRepository customerRepository,
                           @Context OrderCleaningMapper orderCleaningMapper,
                           @Context CleaningRepository cleaningRepository) {
        if (orderRequest == null) {
            return null;
        } else {
            Order order = new Order();
            if (orderRequest.getId() != null){
                order.setId(orderRequest.getId());
            }
            order.setStartDate(orderRequest.getStartDate());
            order.setStartTime(orderRequest.getStartTime());
            order.setCustomer(customerRepository.findById(orderRequest.getCustomerId()).orElseThrow());
            order.setStatus(orderRequest.getStatus());

            for (OrderCleaningRequest orderCleaningRequest : orderRequest.getOrderCleanings()) {
                order.addOrderCleaning(orderCleaningMapper.toEntity(orderCleaningRequest, cleaningRepository));
            }
            order.calculateTotalDuration();
            return order;
        }
    }

    default OrderResponse toResponse(Order order, OrderCleaningMapper orderCleaningMapper) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setStartDate(order.getStartDate());
        orderResponse.setEndDate(order.getEndDate());
        orderResponse.setTotalDuration(order.getTotalDuration());

        orderResponse.setPrice(order.getPrice());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setStartTime(order.getStartTime());
        orderResponse.setOrderCleanings(orderCleaningMapper.toResponseList(order.getOrderCleanings()));
        orderResponse.setCustomerId(order.getCustomer().getId());
        orderResponse.setCustomerName(order.getCustomer().getName() + " " + order.getCustomer().getSurname());
        return orderResponse;
    }

    List<OrderResponse> toResponseList(List<Order> orders);

    default Page<OrderResponse> toPageResponse(Page<Order> orderPage,
                                               @Context OrderCleaningMapper orderCleaningMapper) {
        return orderPage.map(order -> toResponse(order, orderCleaningMapper));
    }

}