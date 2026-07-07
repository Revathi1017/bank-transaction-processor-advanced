package com.transaction.transprocessor.mapper;

import com.transaction.transprocessor.dto.AccountResponse;
import com.transaction.transprocessor.entity.Account;
import org.springframework.stereotype.Service;

@Service
public class AccountResponseMapper {

    public AccountResponse toResponse(Account account) {

        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountID());
        response.setBalance(account.getBalance());
        response.setAccountId(account.getAccountID());

        return response;
    }
}
