package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.exception.GeoapifyProcessingException;
import com.example.cleonoraadmin.entity.AddressOrder;
import com.example.cleonoraadmin.model.order.CustomerAddressRequest;
import com.example.cleonoraadmin.service.GeoapifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class GeoapifyServiceImp implements GeoapifyService {
    private static final String BASE_URL = "https://api.geoapify.com/v1/geocode";
    private final String apiKey = "72af698fdf074aedb40a114de2b12b0b";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeoapifyServiceImp(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AddressOrder processAddress(CustomerAddressRequest request) {
        if (request == null) return null;
        try {
            String url;
            if (request.getLat() != null && request.getLon() != null) {
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/reverse")
                        .queryParam("lat", request.getLat())
                        .queryParam("lon", request.getLon())
                        .queryParam("format", "json")
                        .queryParam("apiKey", apiKey)
                        .queryParam("limit", 1)
                        .encode()
                        .toUriString();
            } else {
                url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/search")
                        .queryParam("housenumber", request.getHouseNumber())
                        .queryParam("street", request.getStreet())
                        .queryParam("city", request.getCity())
                        .queryParam("country", request.getCountry())
                        .queryParam("format", "json")
                        .queryParam("apiKey", apiKey)
                        .queryParam("limit", 1)
                        .encode()
                        .toUriString();
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode resultsNode = jsonNode.get("results");

            if (resultsNode != null && resultsNode.isArray() && !resultsNode.isEmpty()) {
                JsonNode firstResult = resultsNode.get(0);
                AddressOrder addressOrderObject = new AddressOrder();
                addressOrderObject.setCountry(firstResult.get("country").asText());
                addressOrderObject.setCity(firstResult.get("city").asText());
                addressOrderObject.setStreet(firstResult.get("street").asText());
                addressOrderObject.setHouseNumber(firstResult.get("housenumber").asText());
                addressOrderObject.setLon(firstResult.get("lon").asDouble());
                addressOrderObject.setLat(firstResult.get("lat").asDouble());
                return addressOrderObject;
            } else {
                throw new GeoapifyProcessingException("Не удалось найти адрес или координаты.");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error processing address: {}", e.getMessage(), e);
            throw new GeoapifyProcessingException("Проверьте корректность введенных данных.", e);
        }
    }
}
