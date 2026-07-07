package com.transaction.transprocessor.utility;

import com.transaction.transprocessor.domain.type.TransactionType;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.entity.Transaction;
import com.transaction.transprocessor.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionRecorder {

    private final TransactionRepository transactionRepository;

    public TransactionRecorder(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    ////if you want audit records to ALWAYS save
    //// even if main transaction fails:
    //// Creates NEW transaction — commits independently!
    //// Even if deposit() rolls back — audit record saved!
    //// Important for banking audit trail!
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordTransaction(Account account, TransactionType transactionType, BigDecimal requestedAmt) {

        Transaction transaction = new Transaction();

        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAccountId(account.getAccountID());
        transaction.setType(transactionType);
        transaction.setAmount(requestedAmt);
        transaction.setBalanceAfterTransaction(account.getBalance());
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

    }
}
