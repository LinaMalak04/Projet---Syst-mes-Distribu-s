package org.example.ebankservice.services;

import org.example.ebankservice.dtos.BankAccountRequest;
import org.example.ebankservice.entities.AccountOperation;
import org.example.ebankservice.entities.BankAccount;
import org.example.ebankservice.enums.OperationType;
import org.example.ebankservice.exceptions.BalanceNotSufficientException;
import org.example.ebankservice.exceptions.BankAccountNotFoundException;
import org.example.ebankservice.exceptions.InvalidAmountException;
import org.example.ebankservice.repositories.AccountOperationRepository;
import org.example.ebankservice.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository,
                                  AccountOperationRepository accountOperationRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    @Override
    public List<BankAccount> accounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount accountById(String accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(accountId));
    }

    @Override
    public List<BankAccount> accountsByCustomer(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId);
    }

    @Override
    public BankAccount saveAccount(BankAccountRequest request) {
        return bankAccountRepository.save(BankAccount.builder()
                .accountId(UUID.randomUUID().toString())
                .balance(request.balance())
                .createdAt(LocalDateTime.now())
                .currency(request.currency())
                .type(request.type())
                .customerId(request.customerId())
                .build());
    }

    @Override
    public void deleteAccount(String accountId) {
        BankAccount account = accountById(accountId);
        accountOperationRepository.deleteAll(
                accountOperationRepository.findByBankAccountAccountIdOrderByOperationDateDesc(accountId));
        bankAccountRepository.delete(account);
    }

    @Override
    public List<AccountOperation> accountOperations(String accountId) {
        accountById(accountId);
        return accountOperationRepository.findByBankAccountAccountIdOrderByOperationDateDesc(accountId);
    }

    @Override
    public AccountOperation debit(String accountId, double amount, String description) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        BankAccount account = accountById(accountId);
        if (account.getBalance() < amount) throw new BalanceNotSufficientException(accountId);
        account.setBalance(account.getBalance() - amount);
        return saveOperation(account, OperationType.DEBIT, amount, description);
    }

    @Override
    public AccountOperation credit(String accountId, double amount, String description) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        BankAccount account = accountById(accountId);
        account.setBalance(account.getBalance() + amount);
        return saveOperation(account, OperationType.CREDIT, amount, description);
    }

    @Override
    public void transfer(String accountSource, String accountDestination, double amount) {
        debit(accountSource, amount, "Transfer to " + accountDestination);
        credit(accountDestination, amount, "Transfer from " + accountSource);
    }

    private AccountOperation saveOperation(BankAccount account, OperationType type, double amount, String description) {
        return accountOperationRepository.save(AccountOperation.builder()
                .operationDate(LocalDateTime.now())
                .amount(amount)
                .type(type)
                .description(description)
                .bankAccount(account)
                .build());
    }
}
