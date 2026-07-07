package com.transaction.transprocessor.service.factory;

import com.transaction.transprocessor.dto.CreateAccountRequest;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.service.interfaces.AccountFactory;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public abstract class BaseAccountFactory implements AccountFactory {

    @Override
    public Account createAccountOnType(CreateAccountRequest createAccountRequest) {
        Account account = new Account();
        account.setAccountID(UUID.randomUUID());
        account.setAccountNumber(
                String.format("%08d",
                        ThreadLocalRandom.current()
                                .nextInt(10_000_000, 99_999_999)));
        account.setBalance(createAccountRequest.getInitialBalance());
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }
}
