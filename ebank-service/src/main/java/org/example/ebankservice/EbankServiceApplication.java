package org.example.ebankservice;

import org.example.ebankservice.entities.BankAccount;
import org.example.ebankservice.enums.AccountType;
import org.example.ebankservice.repositories.BankAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
public class EbankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(BankAccountRepository bankAccountRepository) {
        return args -> {
            for (long customerId = 1; customerId <= 3; customerId++) {
                for (AccountType type : AccountType.values()) {
                    bankAccountRepository.save(BankAccount.builder()
                            .accountId(UUID.randomUUID().toString())
                            .balance(1000 + Math.round(Math.random() * 90000))
                            .createdAt(LocalDateTime.now())
                            .currency("MAD")
                            .type(type)
                            .customerId(customerId)
                            .build());
                }
            }
        };
    }
}
