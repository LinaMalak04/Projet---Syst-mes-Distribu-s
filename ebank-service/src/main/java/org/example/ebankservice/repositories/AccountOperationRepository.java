package org.example.ebankservice.repositories;

import org.example.ebankservice.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByBankAccountAccountIdOrderByOperationDateDesc(String accountId);
}
