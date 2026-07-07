package com.transaction.transprocessor.service.interfaces;

import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.dto.CreateAccountRequest;
import com.transaction.transprocessor.entity.Account;

public interface AccountFactory {

    AccountType getSupportedAccountType();

    Account createAccountOnType(CreateAccountRequest accountRequest);
}
