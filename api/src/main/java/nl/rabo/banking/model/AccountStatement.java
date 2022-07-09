package nl.rabo.banking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabo.banking.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountStatement {
    String accountNumber;
    BigDecimal currentBalance;
    List<Transaction> transactionHistory;
}