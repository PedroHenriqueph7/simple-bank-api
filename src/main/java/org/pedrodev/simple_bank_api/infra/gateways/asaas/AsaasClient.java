package org.pedrodev.simple_bank_api.infra.gateways.asaas;

import org.pedrodev.simple_bank_api.dtos.BillPaymentRequestDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasBillRequestDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasBillResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "asaasClient", url = "${baseurl.sandbox.api.gateway}", configuration = AsaasConfig.class)
public interface AsaasClient {

    @PostMapping("/v3/bill") // Endpoint de Pagamento de Contas
    AsaasBillResponseDTO pagarConta(@RequestBody AsaasBillRequestDTO request);

}


