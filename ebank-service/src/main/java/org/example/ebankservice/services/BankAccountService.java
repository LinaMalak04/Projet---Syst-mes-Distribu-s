package org.example.ebankservice.services;

import org.example.ebankservice.dtos.BankAccountRequest;
import org.example.ebankservice.entities.AccountOperation;
import org.example.ebankservice.entities.BankAccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccount> accounts();

    BankAccount accountById(String accountId);

    List<BankAccount> accountsByCustomer(Long customerId);

    BankAccount saveAccount(BankAccountRequest request);

    void deleteAccount(String accountId);

    List<AccountOperation> accountOperations(String accountId);

    AccountOperation debit(String accountId, double amount, String description);

    AccountOperation credit(String accountId, double amount, String description);

    void transfer(String accountSource, String accountDestination, double amount);
}
