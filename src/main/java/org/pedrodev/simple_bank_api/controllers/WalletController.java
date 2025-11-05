package org.pedrodev.simple_bank_api.controllers;

import org.pedrodev.simple_bank_api.dtos.WalletResponseDTO;
import org.pedrodev.simple_bank_api.services.WalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @GetMapping(value = "/{userId}")
    public WalletResponseDTO checkWalletBalance(@PathVariable Long userId){
        WalletResponseDTO saldoDTO = walletService.walletGetSaldoFindByUserId(userId);

        return saldoDTO;
    }
}
