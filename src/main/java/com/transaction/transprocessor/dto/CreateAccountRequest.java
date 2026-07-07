package com.transaction.transprocessor.dto;

import com.transaction.transprocessor.domain.type.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

//if client sends null or negative amount,
//@Valid catches it immediately and returns 400 Bad Request
public class CreateAccountRequest {

    @NotNull
    @Positive
    private BigDecimal initialBalance;

    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
