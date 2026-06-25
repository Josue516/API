package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaypalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    private final String BASE_URL = "https://api-m.sandbox.paypal.com"; // URL de pruebas
    
    public String crearOrden(BigDecimal monto, String moneda) {
        try {
            String token = getAccessToken();
            RestClient restClient = RestClient.create();

            Map<String, Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                    "amount", Map.of(
                        "currency_code", moneda,
                        "value", monto.toString()
                    )
                ))
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(BASE_URL + "/v2/checkout/orders")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderRequest)
                    .retrieve()
                    .body(Map.class);

            return response.get("id").toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al crear orden PayPal: " + e.getMessage());
        }
    }

    // Método para obtener el Token de acceso (OAuth2)
    private String getAccessToken() {
        RestClient restClient = RestClient.create();
        
        // PayPal requiere autenticación Basic con ClientID:ClientSecret en Base64
        return restClient.post()
                .uri(BASE_URL + "/v1/oauth2/token")
                .header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString((clientId + ":" + clientSecret).getBytes()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(Map.class)
                .get("access_token").toString();
    }

    // Método para capturar/verificar el pago
    public boolean verificarPago(String orderId) {
        try {
            String token = getAccessToken();
            RestClient restClient = RestClient.create();

            @SuppressWarnings("unchecked")
			Map<String, Object> response = restClient.get()
                    .uri(BASE_URL + "/v2/checkout/orders/" + orderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);

            // Verificamos si el estado de la orden en PayPal es 'COMPLETED'
            return "COMPLETED".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}