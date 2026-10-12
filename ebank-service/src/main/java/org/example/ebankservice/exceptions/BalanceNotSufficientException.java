package org.example.ebankservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BalanceNotSufficientException extends RuntimeException {
    public BalanceNotSufficientException(String accountId) {
        super("Balance not sufficient : " + accountId);
    }
}
