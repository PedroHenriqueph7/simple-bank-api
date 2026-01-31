package org.pedrodev.simple_bank_api.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pedrodev.simple_bank_api.dtos.UserDeletionDTO;
import org.pedrodev.simple_bank_api.dtos.UserRequestUpdateEmailDTO;
import org.pedrodev.simple_bank_api.dtos.UserRequestUpdatePasswordDTO;
import org.pedrodev.simple_bank_api.dtos.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "user-functionalities", description = "Endpoints para alterar informacoes do usuario")
public interface UserControllerDocs {

    @Operation(summary = "Informações do usuario", description = "Retorna o Nome Completo e o email do usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informacoes buscadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Busca não permitida, verifique o token jwt no cabecalho")
    })
    UserResponseDTO findUser(Authentication authentication);

    @Operation(summary = "Atualiza email", description = "Atualiza o email do usuario solicitando a senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualizado email com sucesso"),
            @ApiResponse(responseCode = "403", description = "Atualização não permitida, verifique o token jwt no cabecalho"),
            @ApiResponse(responseCode = "401", description = "Senha incorreta!")
    })
    ResponseEntity<String> updateEmailUser(Authentication authentication, @Valid @RequestBody UserRequestUpdateEmailDTO emailUpdateDTO);

    @Operation(summary = "Atualiza senha", description = "Atualiza senha do usuario verificando a senha atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha autalizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Atualização não permitida, verifique o token jwt no cabecalho"),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta ou nova senha é igual a senha atual ou os campos da nova senha não são iguais")
    })
    ResponseEntity<String> updatePasswordUser(Authentication authentication, UserRequestUpdatePasswordDTO passwordUpdateDTO);

    @Operation(summary = "Deletar Usuario", description = "Deleta usuario verificando a senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso!"),
            @ApiResponse(responseCode = "403", description = "Operação não permitida, verifique o token jwt enviado no cabelho"),
            @ApiResponse(responseCode = "401", description = "Senha invalida")
    })
    ResponseEntity deleteUserWithPasswordConfirmation(Authentication authentication, UserDeletionDTO passwordDTO);
}