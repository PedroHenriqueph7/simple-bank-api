package org.pedrodev.simple_bank_api.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.exceptions.*;
import org.pedrodev.simple_bank_api.infra.gateways.AuthorizationClient;
import org.pedrodev.simple_bank_api.models.Transaction;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.repositories.TransactionRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private final AuthorizationClient authorizationClient;

    private final TemplateEngine templateEngine;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, UserRepository userRepository, AuthorizationClient authorizationClient, TemplateEngine templateEngine) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.authorizationClient = authorizationClient;
        this.templateEngine = templateEngine;
    }

    @Transactional
    public void performTransaction(Authentication authentication, TransactionRequestDTO infoTransaction) {

        User user = (User) authentication.getPrincipal();
        User pagador = userRepository.findById(user.getId()).orElseThrow(()-> new UserNotFoundException());
        User recebedor = userRepository.findById(infoTransaction.recebedor_id()).orElseThrow(()-> new UserNotFoundException());

        if (pagador.getId() == recebedor.getId()) throw new TransactionDeclinedException("Transaction declined! User cannot send money to themselves!");

        Wallet walletPagador = walletRepository.findWalletForUpdateByUserId(pagador.getId()).orElseThrow(() -> new WalletNotFoundException("Payer's wallet not found!!"));

        Wallet walletRecebedor = walletRepository.findWalletForUpdateByUserId(recebedor.getId()).orElseThrow(()-> new WalletNotFoundException("Recipient's wallet not found!!"));

        ZonedDateTime dataLimite = ZonedDateTime.now().minusHours(24);

        BigDecimal valorLimitePorTransacao = BigDecimal.valueOf(1000);
        BigDecimal valorLimiteDiarioDeTransacao = BigDecimal.valueOf(5000);

        BigDecimal valorDebitadoNasUltimas24h = transactionRepository.findValueTransactionsByPagador_id24Hours(pagador.getId(), dataLimite);

        if (infoTransaction.valor().compareTo(valorLimitePorTransacao) > 0) throw new TransferLimitExceededException("The maximum amount allowed per transfer is R$ 1000.00; please request an amount below this limit.");

        if (valorDebitadoNasUltimas24h.compareTo(valorLimiteDiarioDeTransacao) >= 0) throw new DailyLimitExceededException("The daily debit limit is R$ 5000.00. This amount has been exceeded!");


        BigDecimal valorDisponivelParaDebito = valorLimiteDiarioDeTransacao.subtract(valorDebitadoNasUltimas24h);


        if (infoTransaction.valor().compareTo(valorDisponivelParaDebito) > 0) {
            throw new AvailableLimitExceededException(valorDisponivelParaDebito);
        }


        walletPagador.debitar(infoTransaction.valor());

        PoliticaTaxa politicaTaxa = recebedor.getRole().getPoliticaTaxa();

        BigDecimal valorLiquido = politicaTaxa.calcularValorLiquido(infoTransaction.valor());

        walletRecebedor.creditar(valorLiquido);

        Transaction transaction = new Transaction(infoTransaction.valor(), pagador, recebedor, ZonedDateTime.now());

        if (authorizationClient.isAuthorized()) {
            transactionRepository.save(transaction);
        } else {
            throw new TransactionNotAuthorizedException("Transaction not authorized!");
        }
    }

    public byte[] gerarPdfTransacao(Authentication authentication) {

        User userPagador = (User) authentication.getPrincipal();

        Transaction transaction = transactionRepository.findLastTransaction();
        User recebedor = userRepository.findById(transaction.getRecebedor().getId()).orElseThrow(()-> new UserNotFoundException());

        ZonedDateTime dataBrasil = transaction.getDataeHora().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'ás' HH:mm");
        String dataFormatada = dataBrasil.format(formatter);

        String cpfPagadorMascarado = mascararCpfComprovantePagamento(userPagador.getCpf());
        String cpfRecebedorMascarado = mascararCpfComprovantePagamento(recebedor.getCpf());

        Map<String, Object> dadosTransacao = Map.of(
                "valor", transaction.getValor(),
                "data", dataFormatada,
                "idTransacao", transaction.getId(),
                "pagador", userPagador.getNomeCompleto(),
                "cpf_origem", cpfPagadorMascarado,
                "recebedor", recebedor.getNomeCompleto(),
                "cpf_destino", cpfRecebedorMascarado
        );

        // Thymeleaf
        Context context = new Context();
        context.setVariables(dadosTransacao);

        String html = templateEngine.process("comprovante", context);

        try(ByteArrayOutputStream bytesPdf = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(bytesPdf);
            builder.run();

            return bytesPdf.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String mascararCpfComprovantePagamento(String cpf) {

        StringBuilder cpfMascarado = new StringBuilder();
        int tamanhoCpf = 14;

        for(int i = 0;i < tamanhoCpf; i++) {

            if(i <= 2 || i > 11) {
                cpfMascarado.append("*");
            } else {
                cpfMascarado.append(cpf.charAt(i));
            }
        }

        return cpfMascarado.toString();
    }

}
