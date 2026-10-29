package org.example.customerservice.mcp;

import org.example.customerservice.entities.Customer;
import org.example.customerservice.exceptions.CustomerNotFoundException;
import org.example.customerservice.repositories.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMcpTools {
    private final CustomerRepository customerRepository;

    public CustomerMcpTools(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Tool(name = "getAllCustomers", description = "Returns all the customers of the bank (id, first name, last name, email)")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Tool(name = "getCustomerById", description = "Returns one customer from the customer id")
    public Customer getCustomerById(@ToolParam(description = "The customer id") Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Tool(name = "searchCustomers", description = "Searches the customers whose first name or last name contains a keyword")
    public List<Customer> searchCustomers(@ToolParam(description = "A part of the first name or last name") String keyword) {
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }
}
