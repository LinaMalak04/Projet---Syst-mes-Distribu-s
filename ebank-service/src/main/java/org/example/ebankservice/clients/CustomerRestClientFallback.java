package org.example.ebankservice.clients;

import org.example.ebankservice.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Default answers returned when customer-service is unreachable or the circuit is open.
 */
@Component
public class CustomerRestClientFallback implements CustomerRestClient {

    @Override
    public Customer findCustomerById(Long id) {
        return Customer.builder()
                .id(id)
                .firstName("Not Available")
                .lastName("Not Available")
                .email("Not Available")
                .build();
    }

    @Override
    public List<Customer> allCustomers() {
        return List.of();
    }
}
