package nl.rabo.banking.service.impl;

import nl.rabo.banking.controller.request.TransferBalanceRequest;
import nl.rabo.banking.controller.request.WithdrawBalanceRequest;
import nl.rabo.banking.entity.Account;
import nl.rabo.banking.entity.Transaction;
import nl.rabo.banking.exception.AccountDuplicateEntityException;
import nl.rabo.banking.exception.AccountNotFoundException;
import nl.rabo.banking.exception.BalanceNotAvailableAccountException;
import nl.rabo.banking.model.AccountStatement;
import nl.rabo.banking.model.Card;
import nl.rabo.banking.repository.AccountRepository;
import nl.rabo.banking.repository.TransactionRepository;
import nl.rabo.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private static final BigDecimal CHARGE_DIVISION = new BigDecimal(100);
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Account save(Account account) {
        if (findByAccountNumber(account.getAccountNumber()) == null) {
            if (account.getCard().equals(Card.DEBIT.toString()) || account.getCard().equals(Card.CREDIT.toString())) {
                accountRepository.save(account);
                return accountRepository.findByAccountNumberEquals(account.getAccountNumber());
            }
        }
        throw new AccountDuplicateEntityException(account.getAccountNumber(), "Account already exist for accountNumber ");
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumberEquals(accountNumber);
    }


    @Override
    public Transaction transfer(
            TransferBalanceRequest transferBalanceRequest
    ) {
        String fromAccountNumber = transferBalanceRequest.getFromAccountNumber();
        String toAccountNumber = transferBalanceRequest.getToAccountNumber();
        BigDecimal amount = transferBalanceRequest.getAmount();
        Account fromAccount = accountRepository.findByAccountNumberEquals(
                fromAccountNumber
        );
        if (fromAccount == null) {
            throw new AccountNotFoundException(fromAccountNumber, "Could not find for transfer from accountNumber ");
        }

        Account toAccount = accountRepository.findByAccountNumberEquals(toAccountNumber);
        if (toAccount == null) {
            throw new AccountNotFoundException(toAccountNumber, "Could not find for transfer to accountNumber ");
        }

        if (isBalanceAvailableAccount(fromAccount, amount)
        ) {
            fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(amount));
            accountRepository.save(fromAccount);
            if (fromAccount.getCard().equals(Card.CREDIT.name())) {
                chargeCreditCard(fromAccountNumber, amount);
            }
            toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(amount));
            accountRepository.save(toAccount);
            return transactionRepository.save(new Transaction(0L, fromAccountNumber, amount, new Timestamp(System.currentTimeMillis())));
        }
        return null;
    }

    @Override
    public Transaction withdraw(
            WithdrawBalanceRequest withdrawBalanceRequest
    ) {
        String withdrawAccountNumber = withdrawBalanceRequest.getAccountNumber();
        BigDecimal amount = withdrawBalanceRequest.getAmount();

        Account withdrawAccount = accountRepository.findByAccountNumberEquals(
                withdrawAccountNumber
        );
        if (withdrawAccount == null) {
            throw new AccountNotFoundException(withdrawAccountNumber, "Could not find withdraw accountNumber ");
        }

        if (isBalanceAvailableAccount(withdrawAccount, amount)
        ) {
            withdrawAccount.setCurrentBalance(withdrawAccount.getCurrentBalance().subtract(amount));
            Transaction transaction = transactionRepository.save(new Transaction(0L, withdrawAccountNumber, amount, new Timestamp(System.currentTimeMillis())));
            if (withdrawAccount.getCard().equals(Card.CREDIT.name())) {
                chargeCreditCard(withdrawAccountNumber, amount);
            }
            return transaction;
        }
        return null;
    }

    boolean isBalanceAvailableAccount(Account account, BigDecimal amount) {
        BigDecimal totalAmount = amount;

        if (account.getCard().equals(Card.CREDIT.name())) {
            BigDecimal charge = amount.divide(CHARGE_DIVISION);
            totalAmount.add(charge);
        }
        if (account.getCurrentBalance().subtract(totalAmount).compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        throw new BalanceNotAvailableAccountException(account.getAccountNumber(), "Balance is not available for transaction " );
    }

    void chargeCreditCard(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberEquals(
                accountNumber
        );
        BigDecimal charge = amount.divide(CHARGE_DIVISION);
        account.setCurrentBalance(account.getCurrentBalance().subtract(charge));
        transactionRepository.save(new Transaction(0L, account.getAccountNumber(), charge, new Timestamp(System.currentTimeMillis())));

    }

    @Override
    public AccountStatement getStatement(String accountNumber) {
        Account account = accountRepository.findByAccountNumberEquals(accountNumber);
        return new AccountStatement(account.getAccountNumber(), account.getCurrentBalance(), transactionRepository.findByAccountNumberEquals(accountNumber));
    }

    @Override
    public List<AccountStatement> getAllStatements() {
        List<AccountStatement> accountStatements = new ArrayList<>();
        List<Account> accounts = findAll();
        for (Account account : accounts) {
            accountStatements.add(getStatement(account.getAccountNumber()));
        }
        return accountStatements;
    }

}