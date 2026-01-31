package org.pedrodev.simple_bank_api.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.TransactionRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "transactions", description = "Endpoints para operações de Transação entre usuarios")
public interface TransactionsControllerDocs {

    @Operation(summary = "Realizar Transferência", description = "Realizar transferencia entre usuarios(comuns podem enviar, e lojistas apenas receber)")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Transferencia realizada com Sucesso"),
            @ApiResponse(responseCode = "403", description = "Transacao não permitida"),
            @ApiResponse(responseCode = "404", description = "User pagador ou recebedor, ou carteiras não encontradas para transação!"),
            @ApiResponse(responseCode = "422", description = "Transação não realizada por dados invalidados na regra de negocio")
    })
    ResponseEntity<String> performTransaction(Authentication authentication, TransactionRequestDTO infoTransactionDTO);
}
