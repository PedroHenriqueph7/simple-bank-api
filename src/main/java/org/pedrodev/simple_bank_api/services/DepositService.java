package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.SolicitDepositDTO;
import org.pedrodev.simple_bank_api.dtos.SolicitDepositResponseDTO;
import org.pedrodev.simple_bank_api.dtos.WebHookPaymentDTO;
import org.pedrodev.simple_bank_api.exceptions.DepositInvalidException;
import org.pedrodev.simple_bank_api.exceptions.DepositNotFoundException;
import org.pedrodev.simple_bank_api.exceptions.UserNotFoundException;
import org.pedrodev.simple_bank_api.exceptions.WalletNotFoundException;
import org.pedrodev.simple_bank_api.models.Deposit;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.models.enums.Status;
import org.pedrodev.simple_bank_api.repositories.DepositRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class DepositService {

    private DepositRepository depositRepository;
    private WalletRepository walletRepository;

    public DepositService(DepositRepository depositRepository, WalletRepository walletRepository) {

        this.depositRepository = depositRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public SolicitDepositResponseDTO solicitDeposit(Authentication authentication, SolicitDepositDTO infoDepositDTO) {

        User userLogado = (User) authentication.getPrincipal();

        String pixCode = UUID.randomUUID().toString();
        ZonedDateTime dataDeExpiracao = ZonedDateTime.now().plusMinutes(30);

        Deposit deposit = new Deposit(userLogado, infoDepositDTO.valor(), Status.PENDENTE, pixCode, dataDeExpiracao);

        depositRepository.save(deposit);

        return new SolicitDepositResponseDTO(deposit.getPixId(), deposit.getValor(), deposit.getDataExpiracao());
    }

    @Transactional
    public void confirmPayment(WebHookPaymentDTO webHookPaymentDTO) {

        Deposit deposit = depositRepository.findDepositByPixId(webHookPaymentDTO.pixCode()).orElseThrow(()->new DepositNotFoundException("Deposit not found!")) ;

        if (deposit.getStatusAtual() == Status.CONCLUIDO) {
            return;
        }

        ZonedDateTime dataEHorarioPagamentoDeposito = ZonedDateTime.now();

        if (dataEHorarioPagamentoDeposito.isAfter(deposit.getDataExpiracao()) ) {
            deposit.setStatusAtual(Status.EXPIRADO);

            depositRepository.save(deposit);
            throw new DepositInvalidException("Deposit expired!!");
        }


        Wallet walletUser = walletRepository.findByUserId(deposit.getUser().getId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found!!"));

        walletUser.creditar(deposit.getValor());

        deposit.setStatusAtual(Status.CONCLUIDO);

    }
}
