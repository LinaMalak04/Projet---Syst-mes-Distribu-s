package org.example.customerservice;

import org.example.customerservice.entities.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerRepository customerRepository) {
        return args -> customerRepository.saveAll(List.of(
                Customer.builder().firstName("Lina").lastName("Bezzour").email("lina@gmail.com").build(),
                Customer.builder().firstName("Yassine").lastName("Alaoui").email("yassine@gmail.com").build(),
                Customer.builder().firstName("Salma").lastName("Idrissi").email("salma@gmail.com").build()
        ));
    }
}
