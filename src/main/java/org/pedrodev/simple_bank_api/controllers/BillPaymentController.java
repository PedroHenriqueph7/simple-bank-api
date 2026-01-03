package org.pedrodev.simple_bank_api.controllers;

import org.pedrodev.simple_bank_api.dtos.BillPaymentRequestDTO;
import org.pedrodev.simple_bank_api.dtos.BillPaymentResponseDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasWebhookDTO;
import org.pedrodev.simple_bank_api.models.BillPayment;
import org.pedrodev.simple_bank_api.services.BillPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/billpayment")
public class BillPaymentController {

    private BillPaymentService billPaymentService;

    public BillPaymentController(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }


    @PostMapping(value = "/paybill")
    public ResponseEntity<BillPaymentResponseDTO> payBill(Authentication authentication,@RequestBody BillPaymentRequestDTO billPaymentRequestDTO) {

        BillPayment paymentRealizado = billPaymentService.payBill(authentication, billPaymentRequestDTO);

        BillPaymentResponseDTO responseDTO = new BillPaymentResponseDTO(paymentRealizado);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(responseDTO.id()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PostMapping(value = "/webhook/asaas")
    public ResponseEntity<Void> receberNotificacao(@RequestHeader("asaas-access-token") String tokenSecret, @RequestBody AsaasWebhookDTO webhookDto) {

        if (tokenSecret == null || !tokenSecret.equals(webhookDto)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Retorna 401
        }

        if ("PAYMENT_CONFIRMED".equals(webhookDto.event())) {
            billPaymentService.confirmPaymentTicket(webhookDto);
        }
        return ResponseEntity.ok().build();
    }

}
