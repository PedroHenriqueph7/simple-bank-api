package org.pedrodev.simple_bank_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.pedrodev.simple_bank_api.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transactions")
@Tag(name = "Transações", description = "Endpoints para realizar transferencias entre usuarios")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Realizar Transferencia", description = "Transfere valor entre usuarios(lojistas só recebem)")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Transação realizada com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Usuario Pagador ou Recebedor, ou carteira não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados corretos, mas invalidos na regra de negocio"),
            @ApiResponse(responseCode = "403", description = "Autenticado, mas sem permissão")
    })
    @PostMapping(value = "/transaction")
    public ResponseEntity<String> performTransaction(Authentication authentication, @Valid @RequestBody TransactionRequestDTO infoTransactionDTO) {
        transactionService.performTransaction(authentication, infoTransactionDTO);

        return ResponseEntity.ok().body("Transaction completed successfully.");
    }
}
