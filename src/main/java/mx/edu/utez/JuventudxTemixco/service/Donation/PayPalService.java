package mx.edu.utez.JuventudxTemixco.service.Donation;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.JuventudxTemixco.Config.PayPalConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final PayPalConfig config;

    private final WebClient webClient =
            WebClient.builder().build();

    // Access Token para obtener el acceso
    public String getAccessToken() {

        try {

            String credentials = config.getClientId() + ":" + config.getClientSecret();

            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes());

            Map response = webClient.post()
                    .uri(config.getBaseUrl() + "/v1/oauth2/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue("grant_type=client_credentials")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response.get("access_token").toString();

        } catch (WebClientResponseException e) {

            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("BODY: " + e.getResponseBodyAsString());

            throw e;
        }
    }

    // Crea la orden
    public String createOrder(BigDecimal monto) {

        System.out.println("Creando orden de " + monto);

        String token = getAccessToken();

        Map<String, Object> body =
                Map.of(
                        "intent", "CAPTURE",
                        "purchase_units",
                        List.of(
                                Map.of(
                                        "amount",
                                        Map.of(
                                                "currency_code", "MXN",
                                                "value", monto.toPlainString()
                                        )
                                )
                        )
                );

        try {

            Map response =
                    webClient.post()
                            .uri(config.getBaseUrl() + "/v2/checkout/orders")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            return response.get("id").toString();

        } catch (WebClientResponseException e) {

            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("BODY: " + e.getResponseBodyAsString());

            throw e;
        }
    }

    // Captura de la orden
    public Map captureOrder(String orderId) {

        String token = getAccessToken();

        try {

            return webClient.post()
                    .uri(
                            config.getBaseUrl()
                                    + "/v2/checkout/orders/"
                                    + orderId
                                    + "/capture"
                    )
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + token
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE
                    )
                    .bodyValue("{}")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (WebClientRequestException e) {
            System.out.println("Error de conexión con PayPal: "
                    + e.getMessage());
            throw new RuntimeException(
                    "PayPal no respondió. Intenta nuevamente."
            );
        } catch (WebClientResponseException e) {
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("BODY: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public String getCaptureId(Map response) {

        List purchaseUnits =
                (List) response.get("purchase_units");

        Map purchaseUnit =
                (Map) purchaseUnits.get(0);

        Map payments =
                (Map) purchaseUnit.get("payments");

        List captures =
                (List) payments.get("captures");

        Map capture =
                (Map) captures.get(0);

        return capture.get("id").toString();
    }
}
