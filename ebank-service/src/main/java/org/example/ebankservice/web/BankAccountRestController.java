package org.example.ebankservice.web;

import org.example.ebankservice.clients.CustomerRestClient;
import org.example.ebankservice.dtos.BankAccountRequest;
import org.example.ebankservice.dtos.OperationRequest;
import org.example.ebankservice.dtos.TransferRequest;
import org.example.ebankservice.entities.AccountOperation;
import org.example.ebankservice.entities.BankAccount;
import org.example.ebankservice.model.Customer;
import org.example.ebankservice.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class BankAccountRestController {
    private final BankAccountService bankAccountService;
    private final CustomerRestClient customerRestClient;

    public BankAccountRestController(BankAccountService bankAccountService, CustomerRestClient customerRestClient) {
        this.bankAccountService = bankAccountService;
        this.customerRestClient = customerRestClient;
    }

    @GetMapping("/accounts")
    public List<BankAccount> accounts() {
        List<BankAccount> accounts = bankAccountService.accounts();
        Map<Long, Customer> customers = customerRestClient.allCustomers().stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));
        accounts.forEach(account -> account.setCustomer(customers.get(account.getCustomerId())));
        return accounts;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccount accountById(@PathVariable String accountId) {
        BankAccount account = bankAccountService.accountById(accountId);
        account.setCustomer(customerRestClient.findCustomerById(account.getCustomerId()));
        return account;
    }

    @GetMapping("/accounts/customer/{customerId}")
    public List<BankAccount> accountsByCustomer(@PathVariable Long customerId) {
        Customer customer = customerRestClient.findCustomerById(customerId);
        List<BankAccount> accounts = bankAccountService.accountsByCustomer(customerId);
        accounts.forEach(account -> account.setCustomer(customer));
        return accounts;
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccount saveAccount(@RequestBody BankAccountRequest request) {
        return bankAccountService.saveAccount(request);
    }

    @DeleteMapping("/accounts/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable String accountId) {
        bankAccountService.deleteAccount(accountId);
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperation> accountOperations(@PathVariable String accountId) {
        return bankAccountService.accountOperations(accountId);
    }

    @PostMapping("/accounts/{accountId}/debit")
    public AccountOperation debit(@PathVariable String accountId, @RequestBody OperationRequest request) {
        return bankAccountService.debit(accountId, request.amount(), request.description());
    }

    @PostMapping("/accounts/{accountId}/credit")
    public AccountOperation credit(@PathVariable String accountId, @RequestBody OperationRequest request) {
        return bankAccountService.credit(accountId, request.amount(), request.description());
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequest request) {
        bankAccountService.transfer(request.accountSource(), request.accountDestination(), request.amount());
    }
}
