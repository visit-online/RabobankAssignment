package nl.rabo.banking.configuration;

import nl.rabo.banking.entity.Account;
import nl.rabo.banking.model.Card;
import nl.rabo.banking.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    @Bean
    CommandLineRunner initDatabase(AccountService accountService) {

        return args -> {
            log.info("Preloading " + accountService.save(new Account(0l,"1", Card.DEBIT.toString(), BigDecimal.TEN)));
            log.info("Preloading " + accountService.save(new Account(0l,"2", Card.CREDIT.toString(), BigDecimal.TEN)));
        };
    }
}

