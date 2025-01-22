package com.example.cleonoraadmin.model.order;


import com.example.cleonoraadmin.validators.order.LatLonOrAddress;
import lombok.Data;

@Data
@LatLonOrAddress
public class CustomerAddressRequest {
    String country;
    String city;
    String street;
    String houseNumber;
    Double lat;
    Double lon;

}
