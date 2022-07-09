package nl.rabo.banking.service.impl;

import nl.rabo.banking.controller.request.TransferBalanceRequest;
import nl.rabo.banking.controller.request.WithdrawBalanceRequest;
import nl.rabo.banking.entity.Account;
import nl.rabo.banking.exception.BalanceNotAvailableAccountException;
import nl.rabo.banking.model.Card;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountServiceImplTest {

    @TestConfiguration
    static class AccountServiceTestContextConfiguration {
        @Bean("accountServiceImplTest")
        public AccountServiceImpl accountServiceImplTest() {
            return new AccountServiceImpl();

        }
        @Test
        public void sendMoneyTest2() {}
    }

    @Autowired
    @Qualifier("accountServiceImplTest")
    private AccountServiceImpl accountService;
    @Test
    public void create2AccountsWithSameNumber() {
        Account account1 = new Account(0l,"1001", Card.DEBIT.toString(), new BigDecimal(50000));
        Account account2 = new Account(0l,"1001", Card.DEBIT.toString(), new BigDecimal(2000));
        try {
            accountService.save(account1);
            accountService.save(account2);
        } catch (RuntimeException e){
            assertThat(e.getMessage()).isEqualTo("Account already exist for accountNumber 1001");
        }
    }

    @Test
    public void transferMoneyFromDebetCardTest() {
        //new Account(0l,"1", Card.DEBIT.toString(), BigDecimal.TEN))
        Account account1 = new Account(0l,"1001", Card.DEBIT.toString(), new BigDecimal(50000));
        Account account2 = new Account(0l,"2002", Card.DEBIT.toString(), new BigDecimal(2000));
        accountService.save(account1);
        accountService.save(account2);

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(3000)
                );
        accountService.transfer(transferBalanceRequest);
        assertThat(accountService.findByAccountNumber(account1.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(47000));
        assertThat(accountService.findByAccountNumber(account2.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(5000));
    }
    @Test
    public void transferMoneyFromCardBalanceNotAvailableTest() {
        //new Account(0l,"1", Card.DEBIT.toString(), BigDecimal.TEN))
        Account account1 = new Account(0l,"1001", Card.DEBIT.toString(), new BigDecimal(50000));
        Account account2 = new Account(0l,"2002", Card.DEBIT.toString(), new BigDecimal(2000));
        accountService.save(account1);
        accountService.save(account2);

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(51000)
                );
        try {
            accountService.transfer(transferBalanceRequest);
        } catch (BalanceNotAvailableAccountException e){
            assertThat(e.getMessage()).isEqualTo("Balance is not available for transaction 1001");
        }
    }

    @Test
    public void transferMoneyFromCreditCardTest() {
        //new Account(0l,"1", Card.DEBIT.toString(), BigDecimal.TEN))
        Account creditAccount = new Account(0l,"1001", Card.CREDIT.name(), new BigDecimal(50000));
        Account debetAccount = new Account(0l,"2002", Card.DEBIT.name(), new BigDecimal(2000));
        BigDecimal charge = new BigDecimal(30);
        accountService.save(creditAccount);
        accountService.save(debetAccount);

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        creditAccount.getAccountNumber(),
                        debetAccount.getAccountNumber(),
                        new BigDecimal(3000)
                );
        accountService.transfer(transferBalanceRequest);
        assertThat(accountService.findByAccountNumber(creditAccount.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(47000).subtract(charge));
        assertThat(accountService.findByAccountNumber(debetAccount.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(5000));
    }

    @Test
    public void withdrawMoneyFromCreditCardTest() {
        //new Account(0l,"1", Card.DEBIT.toString(), BigDecimal.TEN))
        Account creditAccount = new Account(0l,"1001", Card.CREDIT.name(), new BigDecimal(50000));
        BigDecimal charge = new BigDecimal(30);
        accountService.save(creditAccount);

        WithdrawBalanceRequest withdrawBalanceRequest =
                new WithdrawBalanceRequest(
                        creditAccount.getAccountNumber(),
                        new BigDecimal(3000)
                );
        accountService.withdraw(withdrawBalanceRequest);
        assertThat(accountService.findByAccountNumber(creditAccount.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(47000).subtract(charge));
    }

    @Test
    public void getStatement() {
        Account account1 = new Account(0l,"1001", Card.DEBIT.toString(), new BigDecimal(50000));
        Account account2 = new Account(0l,"2002", Card.DEBIT.toString(), new BigDecimal(2000));
        accountService.save(account1);
        accountService.save(account2);
        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(3000)
                );

        accountService.transfer(transferBalanceRequest);
        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(47000));
        accountService.transfer(transferBalanceRequest);
        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance()).isEqualTo(new BigDecimal(44000));
        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getTransactionHistory().size()).isEqualTo(2);
    }
}