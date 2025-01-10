package com.example.cleanorarest.model.order;

import com.example.cleanorarest.validators.order.LatLonOrAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@LatLonOrAddress
public class CustomerAddressRequest {
    @Schema(example = "Ukraine")
    String country;
    @Schema(example = "Kyiv")
    String city;
    @Schema(example = "Shevchenko")
    String street;
    @Schema(example = "1")
    String houseNumber;
    @Schema(example = "50.007767799999996")
    Double lat;
    @Schema(example = "36.253069818840366")
    Double lon;

}
