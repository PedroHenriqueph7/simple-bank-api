package org.pedrodev.simple_bank_api.web.exception;

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
