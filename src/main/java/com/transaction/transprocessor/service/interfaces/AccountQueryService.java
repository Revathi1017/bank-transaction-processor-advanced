package com.transaction.transprocessor.service.interfaces;

import com.transaction.transprocessor.dto.AccountResponse;
import com.transaction.transprocessor.dto.TransactionResponse;

import java.util.List;

public interface AccountQueryService {

    AccountResponse getAccountById(String accountId);

    List<TransactionResponse> getTransactions(String accountId);
}
