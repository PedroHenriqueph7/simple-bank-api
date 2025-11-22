package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.exceptions.UserNotFoundException;
import org.pedrodev.simple_bank_api.exceptions.WalletNotFoundException;
import org.pedrodev.simple_bank_api.models.Transaction;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.repositories.TransactionRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.lang.Iterable;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void performTransaction(Authentication authentication, TransactionRequestDTO infoTransaction){

        User user = (User) authentication.getPrincipal();
        User pagador = userRepository.findById(user.getId()).orElseThrow(()-> new UserNotFoundException());
        User recebedor = userRepository.findById(infoTransaction.recebedor_id()).orElseThrow(()-> new UserNotFoundException());

        if (pagador.getId() == recebedor.getId()) throw new RuntimeException("Transação recusada!!, Usuario não pode enviar dinheiro para si proprio!!");

        Wallet walletPagador = walletRepository.findByUser_id(pagador.getId());
        if (walletPagador == null) { throw new WalletNotFoundException("Payer's wallet not found!!");}

        Wallet walletRecebedor = walletRepository.findByUser_id(recebedor.getId());
        if (walletRecebedor == null) { throw  new WalletNotFoundException("Recipient's wallet not found!!");}

        ZonedDateTime dataLimite = ZonedDateTime.now().minusHours(24);

        BigDecimal valorLimitePorTransacao = BigDecimal.valueOf(1000);
        BigDecimal valorLimiteDiarioDeTransacao = BigDecimal.valueOf(5000);

        BigDecimal valorDebitadoNasUltimas24h = transactionRepository.findValueTransactionsByPagador_id24Hours(pagador.getId(), dataLimite);

        if (infoTransaction.valor().compareTo(valorLimitePorTransacao) > 0) throw new RuntimeException("O valor limite permitido por transferência é R$ 1000.00, solicite um valor abaixo desse limite");

        if (valorDebitadoNasUltimas24h.compareTo(valorLimiteDiarioDeTransacao) >= 0) throw new RuntimeException("O valor limite permitido para debito diáriamente é de R$ 5000.00, Valor excedido!!");


        BigDecimal valorDisponivelParaDebito = valorLimiteDiarioDeTransacao.subtract(valorDebitadoNasUltimas24h);


        if (infoTransaction.valor().compareTo(valorDisponivelParaDebito) > 0) {
            String mensagemErro = String.format("Valor Excedeu o limite diário, valor disponivel para debito é de R$", valorDisponivelParaDebito);

            throw new RuntimeException(mensagemErro);
        }


        walletPagador.debitar(infoTransaction.valor());

        PoliticaTaxa politicaTaxa = recebedor.getRole().getPoliticaTaxa();

        BigDecimal valorLiquido = politicaTaxa.calcularValorLiquido(infoTransaction.valor());

        walletRecebedor.creditar(valorLiquido);

        Transaction transaction = new Transaction(infoTransaction.valor(), pagador, recebedor, ZonedDateTime.now());

        transactionRepository.save(transaction);

    }
}
