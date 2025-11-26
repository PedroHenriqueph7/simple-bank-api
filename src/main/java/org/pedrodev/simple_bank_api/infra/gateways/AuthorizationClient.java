package org.pedrodev.simple_bank_api.infra.gateways;

import org.pedrodev.simple_bank_api.dtos.AuthorizationResponseDTO;
import org.springframework.stereotype.Component; // Use Component!
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class AuthorizationClient {

    private final WebClient webClient;

    // Injete o WebClient.Builder (Melhor prática)
    public AuthorizationClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://mocki.io/v1/aa7e1ca4-05f0-4e33-aacc-9288d6dab341") // Use a URL real ou do properties
                .build();
    }

    public boolean isAuthorized() {
        try {

            AuthorizationResponseDTO response = webClient.get()
                    .retrieve()
                    .bodyToMono(AuthorizationResponseDTO.class) // Transforma o JSON no seu DTO
                    .block(); // Espera a resposta (Síncrono)

            return response != null && "Autorizado".equalsIgnoreCase(response.message());

        } catch (Exception e) {
            return false;
        }
    }
}
