package nl.rabo.banking.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber,String msg){
        super(msg + accountNumber);
    }

}
