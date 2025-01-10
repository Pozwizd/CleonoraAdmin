package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.Exception.GeoapifyProcessingException;
import com.example.cleanorarest.model.order.CustomerAddressRequest;
import com.example.cleanorarest.entity.AddressOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoapifyServiceImpTest {

    

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GeoapifyServiceImp geoapifyService;

    @Test
    void processAddress_ValidAddress_ReturnsAddressOrder() throws IOException, InterruptedException {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setCity("Kyiv");
        request.setStreet("Tarasa Shevchenka Boulevard");
        request.setHouseNumber("22-24");
        request.setCountry("Ukraine");

        String mockResponse = "{\"results\":[{\"country\":\"Ukraine\",\"city\":\"Kyiv\",\"street\":\"Tarasa Shevchenka Boulevard\",\"housenumber\":\"22-24\",\"lon\":30.506573,\"lat\":50.445078}]}";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponse)).thenReturn(new ObjectMapper().readTree(mockResponse));

        AddressOrder result = geoapifyService.processAddress(request);

        assertThat(result).isNotNull();
        assertThat(result.getCountry()).isEqualTo("Ukraine");
        assertThat(result.getCity()).isEqualTo("Kyiv");
        assertThat(result.getStreet()).isEqualTo("Tarasa Shevchenka Boulevard");
        assertThat(result.getHouseNumber()).isEqualTo("22-24");
        assertThat(result.getLon()).isCloseTo(30.506573, within(1e-6));
        assertThat(result.getLat()).isCloseTo(50.445078, within(1e-6));
    }

    @Test
    void processAddress_ValidCoordinates_ReturnsAddressOrder() throws IOException, InterruptedException {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setLat(50.44507775);
        request.setLon(30.50657312);

        String mockResponse = "{\"results\":[{\"country\":\"Ukraine\",\"city\":\"Kyiv\",\"street\":\"Tarasa Shevchenka Boulevard\",\"housenumber\":\"22-24\",\"lon\":30.506573,\"lat\":50.445078}]}";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponse)).thenReturn(new ObjectMapper().readTree(mockResponse));

        AddressOrder result = geoapifyService.processAddress(request);

        assertThat(result).isNotNull();
        assertThat(result.getCountry()).isEqualTo("Ukraine");
        assertThat(result.getCity()).isEqualTo("Kyiv");
        assertThat(result.getStreet()).isEqualTo("Tarasa Shevchenka Boulevard");
        assertThat(result.getHouseNumber()).isEqualTo("22-24");
        assertThat(result.getLon()).isCloseTo(30.506573, within(1e-6));
        assertThat(result.getLat()).isCloseTo(50.445078, within(1e-6));
    }

    @Test
    void processAddress_NullRequest_ReturnsNull() {

        AddressOrder result = geoapifyService.processAddress(null);

        assertThat(result).isNull();
    }

    @Test
    void processAddress_NoResultsFound_ThrowsGeoapifyProcessingException() throws IOException, InterruptedException {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setCity("Nonexistent City");
        request.setStreet("Nonexistent Street");
        request.setHouseNumber("0000");
        request.setCountry("Nowhere");

        String mockResponse = "{\"results\":[]}";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponse)).thenReturn(new ObjectMapper().readTree(mockResponse));

        assertThatThrownBy(() -> geoapifyService.processAddress(request))
                .isInstanceOf(GeoapifyProcessingException.class)
                .hasMessageContaining("Не удалось найти адрес или координаты.");
    }

    @Test
    void processAddress_IOException_ThrowsGeoapifyProcessingException() throws IOException, InterruptedException {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setCity("Test City");
        request.setStreet("Test Street");
        request.setHouseNumber("123");
        request.setCountry("Test Country");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenThrow(new IOException("Simulated IO exception"));

        assertThatThrownBy(() -> geoapifyService.processAddress(request))
                .isInstanceOf(GeoapifyProcessingException.class)
                .hasMessageContaining("Проверьте корректность введенных данных.");
    }

    @Test
    void processAddress_InterruptedException_ThrowsGeoapifyProcessingException() throws IOException, InterruptedException {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setCity("Test City");
        request.setStreet("Test Street");
        request.setHouseNumber("123");
        request.setCountry("Test Country");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenThrow(new InterruptedException("Simulated interruption"));

        assertThatThrownBy(() -> geoapifyService.processAddress(request))
                .isInstanceOf(GeoapifyProcessingException.class)
                .hasMessageContaining("Проверьте корректность введенных данных.");
    }


}
