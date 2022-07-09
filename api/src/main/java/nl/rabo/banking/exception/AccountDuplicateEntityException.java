package nl.rabo.banking.exception;

public class AccountDuplicateEntityException extends RuntimeException {
    public AccountDuplicateEntityException(String accountNumber, String msg){
        super(msg + accountNumber);
    }

}
