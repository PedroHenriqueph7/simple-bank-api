package org.pedrodev.simple_bank_api.infra.gateways.asaas;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AsaasConfig {

    @Value("${api.gateway.secret}")
    private String asaasApiKey;

    @Value("${api.gateway.user.agent}")
    private String userAgent;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Injeta o header automaticamente em TODA requisição desse cliente
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("User-Agent", userAgent);
            requestTemplate.header("access_token", asaasApiKey);

        };
    }

}
