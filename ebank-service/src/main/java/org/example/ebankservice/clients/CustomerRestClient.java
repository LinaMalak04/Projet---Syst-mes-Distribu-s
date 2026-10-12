package org.example.ebankservice.clients;

import org.example.ebankservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    Customer findCustomerById(@PathVariable("id") Long id);

    @GetMapping("/customers")
    List<Customer> allCustomers();
}
