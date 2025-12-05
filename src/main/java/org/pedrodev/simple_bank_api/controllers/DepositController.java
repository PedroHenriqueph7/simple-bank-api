package org.pedrodev.simple_bank_api.controllers;

import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.SolicitDepositDTO;
import org.pedrodev.simple_bank_api.dtos.SolicitDepositResponseDTO;
import org.pedrodev.simple_bank_api.dtos.WebHookPaymentDTO;
import org.pedrodev.simple_bank_api.services.DepositService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/deposits")
public class DepositController {

    @Value("${api.security.webhook.secret}")
    private String webHookSecret;

    private final DepositService depositService;

    public DepositController(DepositService depositService) {

        this.depositService = depositService;
    }


    @PostMapping(value = "/deposit")
    public ResponseEntity<SolicitDepositResponseDTO>  solicitarDeposito(Authentication authentication,@RequestBody @Valid SolicitDepositDTO soliciteDepositDTO){

        var responseDTO = depositService.solicitDeposit(authentication, soliciteDepositDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PatchMapping(value = "/webhook/payment")
    public ResponseEntity<Void> confirmarPagamento(@RequestHeader("x-callback-token") String security_key_secret, @RequestBody WebHookPaymentDTO webHookPaymentDTO) {

        if (!webHookSecret.equals(security_key_secret)) {
            return ResponseEntity.status(403).build();
        }

        depositService.confirmPayment(webHookPaymentDTO);

        return ResponseEntity.ok().build();
    }

}
