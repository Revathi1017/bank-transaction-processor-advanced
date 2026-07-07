package com.transaction.transprocessor.service.interfaces;

import com.transaction.transprocessor.dto.AccountResponse;
import com.transaction.transprocessor.dto.CreateAccountRequest;
import com.transaction.transprocessor.dto.MoneyRequest;
import com.transaction.transprocessor.dto.TransferRequest;

public interface AccountCommandService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse deposit(String accountId, MoneyRequest request);

    AccountResponse withdraw(String accountId, MoneyRequest request);

    AccountResponse transfer(TransferRequest request);

}
