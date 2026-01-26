package org.pedrodev.simple_bank_api.services;

import org.pedrodev.simple_bank_api.exceptions.*;
import org.pedrodev.simple_bank_api.dtos.BillPaymentRequestDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.AsaasClient;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasBillRequestDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasBillResponseDTO;
import org.pedrodev.simple_bank_api.infra.gateways.asaas.dto.AsaasWebhookDTO;
import org.pedrodev.simple_bank_api.models.BillPayment;
import org.pedrodev.simple_bank_api.models.User;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.pedrodev.simple_bank_api.models.enums.PaymentStatus;
import org.pedrodev.simple_bank_api.repositories.BillPaymentRepository;
import org.pedrodev.simple_bank_api.repositories.UserRepository;
import org.pedrodev.simple_bank_api.repositories.WalletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BillPaymentService {

    private BillPaymentRepository billPaymentRepository;
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private AsaasClient asaasClient;

    public BillPaymentService(BillPaymentRepository billPaymentRepository, UserRepository userRepository, WalletRepository walletRepository, AsaasClient asaasClient) {

        this.billPaymentRepository = billPaymentRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.asaasClient = asaasClient;

    }

    @Transactional
    public BillPayment payBill(Authentication authentication, BillPaymentRequestDTO billPaymentRequestDTO) {

        User user = (User) authentication.getPrincipal();
        User payer = userRepository.findById(user.getId()).orElseThrow(()-> new UserNotFoundException());

        Wallet walletPayer = walletRepository.findWalletForUpdateByUserId(payer.getId()).orElseThrow(()-> new WalletNotFoundException());

        Boolean jaPago = billPaymentRepository.existsByIdentificationFieldAndPaymentStatus(billPaymentRequestDTO.identificationField(), PaymentStatus.PAID);

        if (jaPago) { throw new ThisInvoiceHasAlreadyBeenPaidException();}

        if (walletPayer.getSaldo().compareTo(billPaymentRequestDTO.value()) < 0) {
            throw new InsufficientWalletBalance();
        }

        walletPayer.debitar(billPaymentRequestDTO.value());
        walletRepository.save(walletPayer);

        AsaasBillRequestDTO asaasBillRequestDTO = new AsaasBillRequestDTO(billPaymentRequestDTO.identificationField(), billPaymentRequestDTO.value(), billPaymentRequestDTO.description(), billPaymentRequestDTO.scheduleDate());

        try {

            AsaasBillResponseDTO asaasBillResponseDTO = asaasClient.pagarConta(asaasBillRequestDTO);

            BillPayment billPayment = new BillPayment();

            billPayment.setIdentificationField(asaasBillResponseDTO.identificationField());
            billPayment.setBeneficiary(asaasBillResponseDTO.companyName());
            billPayment.setDescription(asaasBillRequestDTO.description());
            billPayment.setPayer(payer);
            billPayment.setFee(asaasBillResponseDTO.fee());
            billPayment.setFine(asaasBillResponseDTO.fine());
            billPayment.setDiscount(asaasBillResponseDTO.discount());
            billPayment.setValue(asaasBillResponseDTO.value());
            billPayment.setInterest(asaasBillResponseDTO.interest());
            billPayment.setReceiptUrl(asaasBillResponseDTO.transactionReceiptUrl());
            billPayment.setExternalReference(asaasBillResponseDTO.id());

            try {
                billPayment.setPaymentStatus(PaymentStatus.valueOf(asaasBillResponseDTO.status()));

            } catch (IllegalArgumentException e) {
                billPayment.setPaymentStatus(PaymentStatus.PENDING);
            }

            if (asaasBillResponseDTO.dueDate() != null) {
                billPayment.setOriginalDueDate(LocalDate.parse(asaasBillResponseDTO.dueDate()));
            }

            return billPaymentRepository.save(billPayment);

        } catch (Exception e) {
            // Rollback automático pelo @Transactional
            throw new RuntimeException("Erro no pagamento: " + e.getMessage());
        }

    }


    @Transactional
    public void confirmPaymentTicket(AsaasWebhookDTO webhookDto){

        if (!webhookDto.event().startsWith("BILL_PAID")) {
            return;
        }

        String idAsaas = webhookDto.bill().id();

        BillPayment ticket = billPaymentRepository.findByExternalReference(idAsaas).orElseThrow(()-> new PaymentSlipNotFoundException());

        if (PaymentStatus.PAID.equals(ticket.getPaymentStatus())) return;

        ticket.setPaymentStatus(PaymentStatus.PAID);
        billPaymentRepository.save(ticket);

        System.out.println("PAGAMENTO DE CONTA CONFIRMADO: " + idAsaas);

    }

    @Transactional
    public void reversePayment(AsaasWebhookDTO webhookDTO) {

        String idAsaas = webhookDTO.bill().id();
       
        BillPayment conta = billPaymentRepository.findByExternalReference(idAsaas).orElseThrow(() -> new PaymentSlipNotFoundException());

        if (PaymentStatus.REFUNDED.equals(conta.getPaymentStatus())) return;


        User user = conta.getPayer();
        Wallet walletPayer = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WalletNotFoundException());

        walletPayer.creditar(conta.getValue());
        walletRepository.save(walletPayer);

        conta.setPaymentStatus(PaymentStatus.REFUNDED);
        billPaymentRepository.save(conta);

        System.out.println("ESTORNO REALIZADO!");

    }

}