package org.pedrodev.simple_bank_api.web.exception;

import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.pedrodev.simple_bank_api.exceptions.*;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    //Capturo a exception
    // Trato e
    // Retorno a mensagem com o status

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException userNotFoundException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, userNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(treatResponse);
    }

    @ExceptionHandler(DeactivatedUserException.class)
    protected ResponseEntity<RestErrorMessage> deactivatedUserHandler(DeactivatedUserException deactivatedUserException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.FORBIDDEN, deactivatedUserException.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(treatResponse);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    protected  ResponseEntity<RestErrorMessage> invalidPasswordHandler(InvalidPasswordException invalidPasswordException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNAUTHORIZED, invalidPasswordException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(treatResponse);
    }

    @ExceptionHandler(TheUserAlreadyHasAnAccountException.class)
    protected  ResponseEntity<RestErrorMessage> theUserAlreadyHasAnAccountHandler(TheUserAlreadyHasAnAccountException theUserAlreadyHasAnAccountException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, theUserAlreadyHasAnAccountException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(treatResponse);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    protected ResponseEntity<RestErrorMessage> walletNotFoundHandler(WalletNotFoundException walletNotFoundException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, walletNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(treatResponse);
    }

    @ExceptionHandler(TransactionDeclinedException.class)
    protected  ResponseEntity<RestErrorMessage> transactionDeclineHandler(TransactionDeclinedException transactionDeclinedException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, transactionDeclinedException.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(TransferLimitExceededException.class)
    protected ResponseEntity<RestErrorMessage> transferLimitExceededHandler(TransferLimitExceededException transferLimitExceededException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, transferLimitExceededException.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    protected ResponseEntity<RestErrorMessage> dailyLimitExceededException(DailyLimitExceededException dailyLimitExceededException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, dailyLimitExceededException.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(AvailableLimitExceededException.class)
    protected ResponseEntity<RestErrorMessage> availableLimitExceededHandler(AvailableLimitExceededException availableLimitExceededException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Available limit exceeded, limit still available for debit R$ %.2f", availableLimitExceededException.getValorDisponivel()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    protected ResponseEntity<RestErrorMessage> pessimisticEntityLockHandler() {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "The wallet is currently processing another transaction. Please wait a few moments and try again.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(treatResponse);
    }

    @ExceptionHandler(TransactionNotAuthorizedException.class)
    protected  ResponseEntity<RestErrorMessage> transactionNotAuthorizedHandler(TransactionNotAuthorizedException transactionNotAuthorizedException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.FORBIDDEN, transactionNotAuthorizedException.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(treatResponse);
    }

    @ExceptionHandler(DepositNotFoundException.class)
    protected ResponseEntity<RestErrorMessage> depositNotFoundHandler(DepositNotFoundException depositNotFoundException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, depositNotFoundException.getMessage());

        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(treatResponse);
    }

    @ExceptionHandler(DepositInvalidException.class)
    protected  ResponseEntity<RestErrorMessage> depositInvalidHandler(DepositInvalidException depositInvalidException) {

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, depositInvalidException.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(ThisInvoiceHasAlreadyBeenPaidException.class)
    protected  ResponseEntity<RestErrorMessage> thisInvoiceHasAlreadyBeenPaidHandler(ThisInvoiceHasAlreadyBeenPaidException exception){

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(InsufficientWalletBalance.class)
    protected ResponseEntity<RestErrorMessage> insufficientWalletBalanceHandler(InsufficientWalletBalance exception){

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(treatResponse);
    }

    @ExceptionHandler(IllegalInputArgumentException.class)
    protected ResponseEntity<RestErrorMessage> illegalInputArgumentHandler(IllegalArgumentException exception){

        RestErrorMessage treatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(treatResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2 // merge caso o mesmo campo tenha >1 erro
                ));

        return ResponseEntity.status(status).body(errors);
    }
}
