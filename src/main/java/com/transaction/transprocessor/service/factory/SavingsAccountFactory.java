package com.transaction.transprocessor.service.factory;

import com.transaction.transprocessor.domain.type.AccountStatus;
import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.dto.CreateAccountRequest;
import com.transaction.transprocessor.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class SavingsAccountFactory extends BaseAccountFactory {

    @Override
    public AccountType getSupportedAccountType() {
        return AccountType.SAVINGS;
    }

    @Override
    public Account createAccountOnType(CreateAccountRequest accountRequest) {
        // Call PARENT first — common fields!
        Account account = super.createAccountOnType(accountRequest);

        // Then add savings specific fields!
        account.setAccountType(getSupportedAccountType());
        account.setStatus(AccountStatus.OPEN);
        //Can set some interest rate and daily limit as well
        return account;
    }
}
