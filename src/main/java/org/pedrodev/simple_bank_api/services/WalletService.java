package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.WalletResponseDTO;
import org.pedrodev.simple_bank_api.exceptions.WalletNotFoundException;
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

        BigDecimal saldo = BigDecimal.ZERO;

        Wallet newWallet = new Wallet(saldo,objetoUser);
        walletRepository.save(newWallet);
    }

    @Transactional
    public WalletResponseDTO walletGetSaldoFindByUserId(Long userId) {

        Wallet wallet = walletRepository.findByUser_id(userId);
        return new WalletResponseDTO(wallet.getSaldo());
    }

    @Transactional
    public void updateWalletForDeactivatedUser(User user){

        Wallet wallet = walletRepository.findByUser_id(user.getId());

        if (wallet == null) throw  new WalletNotFoundException("Wallet not found!");

        wallet.setSaldo(BigDecimal.ZERO);
        walletRepository.save(wallet);
    }

}
