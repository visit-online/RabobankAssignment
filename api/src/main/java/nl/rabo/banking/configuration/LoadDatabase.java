package nl.rabo.banking.configuration;

import nl.rabo.banking.entity.Account;
import nl.rabo.banking.model.Card;
import nl.rabo.banking.service.impl.AccountServiceImpl;
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
    CommandLineRunner initDatabase(AccountServiceImpl accountServiceImpl) {

        return args -> {
            log.info("Preloading {}", accountServiceImpl.save(new Account(0L,"1", Card.DEBIT.name(), BigDecimal.TEN)));
            log.info("Preloading {}", accountServiceImpl.save(new Account(0L,"2", Card.CREDIT.name(), BigDecimal.TEN)));
        };
    }
}

