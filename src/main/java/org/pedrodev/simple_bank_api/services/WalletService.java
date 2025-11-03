package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository){
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void automaticallyAddWalletToUserRegistration(User objetoUser) {

        BigDecimal saldo = BigDecimal.valueOf(0.00);

        Wallet wallet = new Wallet(saldo,objetoUser);
        walletRepository.save(wallet);
    }

}
