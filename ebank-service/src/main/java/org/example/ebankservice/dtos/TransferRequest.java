package org.example.ebankservice.dtos;

public record TransferRequest(String accountSource, String accountDestination, double amount) {
}
