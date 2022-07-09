package nl.rabo.banking.service;


import nl.rabo.banking.controller.request.TransferBalanceRequest;
import nl.rabo.banking.controller.request.WithdrawBalanceRequest;
import nl.rabo.banking.entity.Account;
import nl.rabo.banking.entity.Transaction;
import nl.rabo.banking.model.AccountStatement;

import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account save(Account account);
    Transaction transfer(
            TransferBalanceRequest transferBalanceRequest
    );
    Transaction withdraw(
            WithdrawBalanceRequest withdrawBalanceRequest
    );
    AccountStatement getStatement(String accountNumber);

    List<AccountStatement> getAllStatements();

}