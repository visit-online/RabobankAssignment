package nl.rabo.banking.exception;

public class BalanceNotAvailableAccountException extends RuntimeException {
    public BalanceNotAvailableAccountException(String accountNumber, String msg){
        super(msg + accountNumber);
    }

}
