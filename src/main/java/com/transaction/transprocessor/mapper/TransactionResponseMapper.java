package com.transaction.transprocessor.mapper;

import com.transaction.transprocessor.dto.TransactionResponse;
import com.transaction.transprocessor.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionResponseMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();

        response.setTransactionId(transaction.getTransactionId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfterTransaction(
                transaction.getBalanceAfterTransaction()
        );
        response.setTimestamp(transaction.getTimestamp());

        return response;
    }
}
