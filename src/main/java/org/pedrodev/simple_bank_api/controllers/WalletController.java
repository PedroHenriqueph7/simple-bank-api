package org.pedrodev.simple_bank_api.controllers;

import org.pedrodev.simple_bank_api.dtos.WalletResponseDTO;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.services.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @GetMapping()
    public WalletResponseDTO checkWalletBalance(Authentication authentication){

        User userResponsible = (User) authentication.getPrincipal();
        WalletResponseDTO saldoDTO = walletService.walletGetSaldoFindByUserId(userResponsible.getId());

        return saldoDTO;
    }
}
