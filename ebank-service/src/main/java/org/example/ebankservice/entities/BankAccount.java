package org.example.ebankservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ebankservice.enums.AccountType;
import org.example.ebankservice.model.Customer;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccount {
    @Id
    private String accountId;
    private double balance;
    private LocalDateTime createdAt;
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    private Long customerId;
    @Transient
    private Customer customer;
}
