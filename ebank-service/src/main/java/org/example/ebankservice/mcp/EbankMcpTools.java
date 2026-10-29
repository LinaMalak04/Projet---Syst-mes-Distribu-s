package org.example.ebankservice.mcp;

import org.example.ebankservice.clients.CustomerRestClient;
import org.example.ebankservice.entities.AccountOperation;
import org.example.ebankservice.entities.BankAccount;
import org.example.ebankservice.services.BankAccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EbankMcpTools {
    private final BankAccountService bankAccountService;
    private final CustomerRestClient customerRestClient;

    public EbankMcpTools(BankAccountService bankAccountService, CustomerRestClient customerRestClient) {
        this.bankAccountService = bankAccountService;
        this.customerRestClient = customerRestClient;
    }

    @Tool(name = "getAllAccounts", description = "Returns all the bank accounts with their balance, currency, type and customer id")
    public List<BankAccount> getAllAccounts() {
        return bankAccountService.accounts();
    }

    @Tool(name = "getAccountById", description = "Returns one bank account and its owner from the account id")
    public BankAccount getAccountById(@ToolParam(description = "The account id (UUID)") String accountId) {
        BankAccount account = bankAccountService.accountById(accountId);
        account.setCustomer(customerRestClient.findCustomerById(account.getCustomerId()));
        return account;
    }

    @Tool(name = "getCustomerAccounts", description = "Returns the bank accounts of a customer from the customer id")
    public List<BankAccount> getCustomerAccounts(@ToolParam(description = "The customer id") Long customerId) {
        return bankAccountService.accountsByCustomer(customerId);
    }

    @Tool(name = "getAccountOperations", description = "Returns the history of the operations (debits and credits) of an account, most recent first")
    public List<AccountOperation> getAccountOperations(@ToolParam(description = "The account id (UUID)") String accountId) {
        return bankAccountService.accountOperations(accountId);
    }

    @Tool(name = "creditAccount", description = "Credits (deposits money on) a bank account")
    public AccountOperation creditAccount(@ToolParam(description = "The account id (UUID)") String accountId,
                                          @ToolParam(description = "The amount, strictly positive") double amount,
                                          @ToolParam(description = "A short description of the operation") String description) {
        return bankAccountService.credit(accountId, amount, description);
    }

    @Tool(name = "debitAccount", description = "Debits (withdraws money from) a bank account. Fails if the balance is not sufficient")
    public AccountOperation debitAccount(@ToolParam(description = "The account id (UUID)") String accountId,
                                         @ToolParam(description = "The amount, strictly positive") double amount,
                                         @ToolParam(description = "A short description of the operation") String description) {
        return bankAccountService.debit(accountId, amount, description);
    }

    @Tool(name = "transfer", description = "Transfers an amount from a source account to a destination account")
    public String transfer(@ToolParam(description = "The source account id (UUID)") String accountSource,
                           @ToolParam(description = "The destination account id (UUID)") String accountDestination,
                           @ToolParam(description = "The amount, strictly positive") double amount) {
        bankAccountService.transfer(accountSource, accountDestination, amount);
        return "Transfer of " + amount + " from " + accountSource + " to " + accountDestination + " done";
    }
}
