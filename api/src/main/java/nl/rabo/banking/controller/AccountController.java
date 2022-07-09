package nl.rabo.banking.controller;

import nl.rabo.banking.controller.request.AccountStatementRequest;
import nl.rabo.banking.controller.request.TransferBalanceRequest;
import nl.rabo.banking.controller.request.WithdrawBalanceRequest;
import nl.rabo.banking.entity.Account;
import nl.rabo.banking.model.response.Response;
import nl.rabo.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @RequestMapping("/create")
    public List<Account> create(@RequestBody Account account) {
        accountService.save(account);
        return accountService.findAll();
    }

    @RequestMapping("/all")
    public List<Account> all() {
        return accountService.findAll();
    }

    @RequestMapping("/transfer")
    public Response transfer(
            @RequestBody TransferBalanceRequest transferBalanceRequest
    ) {
            return Response.ok().setPayload(accountService.transfer(transferBalanceRequest));
    }

    @RequestMapping("/withdraw")
    public Response transfer(
            @RequestBody WithdrawBalanceRequest withdrawBalanceRequest
    ) {
            return Response.ok().setPayload(accountService.withdraw(withdrawBalanceRequest));
    }

    @RequestMapping("/balance")
    public Response getStatement(
            @RequestBody AccountStatementRequest accountStatementRequest

    ) {
        return Response.ok().setPayload(
                accountService.getStatement(accountStatementRequest.getAccountNumber())
        );

    }

    @RequestMapping("/all-balance")
    public Response getAllStatements() {
        return Response.ok().setPayload(
                accountService.getAllStatements()
        );

    }

}