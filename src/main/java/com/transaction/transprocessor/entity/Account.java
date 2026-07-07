package com.transaction.transprocessor.entity;

import com.transaction.transprocessor.domain.type.AccountStatus;
import com.transaction.transprocessor.domain.type.AccountType;
import jakarta.persistence.*;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Account")
public class Account {

    @Setter
    @Id
    private UUID accountID;

    @Setter
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Setter
    @Column(nullable = false, precision = 19, scale = 2) //99999999999999999.99
    private BigDecimal balance;

    @Setter
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Setter
    private AccountStatus status;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public UUID getAccountID() {
        return accountID;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
