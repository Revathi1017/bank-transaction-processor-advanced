package com.transaction.transprocessor.service.interfaces;


import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.entity.Account;

import java.math.BigDecimal;

public interface AccountStrategy {

    AccountType supportedAccountType();

    void validateDeposit(Account account, BigDecimal amount);

    void validateWithdrawal(Account account, BigDecimal amount);
}
