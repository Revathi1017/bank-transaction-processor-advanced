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
@Table(name = "accounts")
public class Account {

    @Setter
    @Id
    @Column(name="account_id")
    private UUID accountID;

    @Setter
    @Column(name="accountNumber", nullable = false, unique = true)
    private String accountNumber;

    @Setter
    @Column(name="balance", nullable = false, precision = 19, scale = 2) //99999999999999999.99
    private BigDecimal balance;

    @Setter
    @Column(name="account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Setter
    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Setter
    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Version
    @Column(name="version")
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
