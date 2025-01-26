package com.example.cleonoraadmin.service;


import com.example.cleonoraadmin.entity.AddressOrder;
import com.example.cleonoraadmin.model.order.CustomerAddressRequest;

public interface GeoapifyService {
    AddressOrder processAddress(CustomerAddressRequest request);
}
