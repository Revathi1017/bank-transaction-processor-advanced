package com.transaction.transprocessor.service.strategy;

import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.exception.InsufficientFundsException;
import com.transaction.transprocessor.service.interfaces.AccountStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SavingsAccountStrategy implements AccountStrategy {

    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    @Override
    public AccountType supportedAccountType() {
        return AccountType.SAVINGS;
    }

    @Override
    public void validateDeposit(Account account, BigDecimal amount) {
        //No deposit restrictions on savings just return!
    }

    @Override
    public void validateWithdrawal(Account account, BigDecimal amount) {

        BigDecimal balanceAfter = account.getBalance().subtract(amount);
        if(balanceAfter.compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientFundsException("Savings account must maintain minimum balance"+
                    account.getBalance().subtract(MINIMUM_BALANCE));
        }
    }
}
