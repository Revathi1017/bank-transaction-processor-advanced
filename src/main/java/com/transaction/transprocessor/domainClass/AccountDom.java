package com.transaction.transprocessor.domainClass;

import com.transaction.transprocessor.domain.type.AccountStatus;
import com.transaction.transprocessor.domain.type.AccountType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AccountDom {

    @Id
    private UUID accountID;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

    private AccountStatus status;

    private LocalDateTime createdAt;

    // Rich behaviour — domain logic lives here!

    // Getters only — no setters for business fields!
}
