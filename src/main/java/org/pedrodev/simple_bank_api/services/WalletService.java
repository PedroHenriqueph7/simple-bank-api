package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.WalletResponseDTO;
import org.pedrodev.simple_bank_api.exceptions.WalletNotFoundException;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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

        Optional<Wallet> wallet = Optional.ofNullable(walletRepository.findByUserId(userId).orElseThrow(()-> new WalletNotFoundException("Wallet not found!")));
        return new WalletResponseDTO(wallet.get().getSaldo());
    }

    @Transactional
    public void updateWalletForDeactivatedUser(User user){

        Optional<Wallet> wallet = Optional.ofNullable(walletRepository.findByUserId(user.getId()).orElseThrow(()-> new WalletNotFoundException("Wallet not found!")));

        wallet.get().setSaldo(BigDecimal.ZERO);
        walletRepository.save(wallet.get());
    }

}
