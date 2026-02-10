package org.pedrodev.simple_bank_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.controllers.docs.TransactionsControllerDocs;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.services.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController implements TransactionsControllerDocs {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    @PostMapping(value = "/transaction")
    public ResponseEntity<String> performTransaction(Authentication authentication, @Valid @RequestBody TransactionRequestDTO infoTransactionDTO) {
        transactionService.performTransaction(authentication, infoTransactionDTO);

        return ResponseEntity.ok().body("Transaction completed successfully.");
    }

    @GetMapping(value = "/me/comprovante")
    public ResponseEntity<byte[]> gerarPdf(Authentication authentication) {

        byte[] pdfBytes = transactionService.gerarPdfTransacao(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=comprovante.pdf")
                .body(pdfBytes);
    }
}
