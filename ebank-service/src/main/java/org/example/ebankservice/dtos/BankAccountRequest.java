package org.example.ebankservice.dtos;

import org.example.ebankservice.enums.AccountType;

public record BankAccountRequest(double balance, String currency, AccountType type, Long customerId) {
}
