package nl.rabo.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountDuplicateEntityAdvice {
    @ResponseBody
    @ExceptionHandler(AccountDuplicateEntityException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    String accountDuplicateEntityHandler(AccountDuplicateEntityException ex) {
        return ex.getMessage();
    }
}
