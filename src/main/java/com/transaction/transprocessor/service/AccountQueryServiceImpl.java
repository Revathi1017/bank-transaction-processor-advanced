package com.transaction.transprocessor.service;

import com.transaction.transprocessor.dto.*;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.entity.Transaction;
import com.transaction.transprocessor.exception.AccountNotFoundException;
import com.transaction.transprocessor.mapper.AccountResponseMapper;
import com.transaction.transprocessor.mapper.TransactionResponseMapper;
import com.transaction.transprocessor.repository.AccountRepository;
import com.transaction.transprocessor.repository.TransactionRepository;
import com.transaction.transprocessor.service.interfaces.AccountQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final AccountResponseMapper accountResponseMapper;

    private final TransactionResponseMapper transactionResponseMapper;

    @Autowired
    public AccountQueryServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                                   AccountResponseMapper accountResponseMapper,
                                   TransactionResponseMapper transactionResponseMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountResponseMapper = accountResponseMapper;
        this.transactionResponseMapper= transactionResponseMapper;
    }

    // Why readOnly = true?
    // Tells DB — no writes happening!
    // DB can optimise — use read replica!
    // Prevents accidental writes!
    // Better performance!
    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(String accountId) {

        UUID uuid = UUID.fromString(accountId);
        Account account = accountRepository.findById(uuid)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        return accountResponseMapper.toResponse(account);
    }

    // Why readOnly = true?
    // Tells DB — no writes happening!
    // DB can optimise — use read replica!
    // Prevents accidental writes!
    // Better performance!
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(String accountId) {

        accountRepository.findById(UUID.fromString(accountId)).orElseThrow(() ->
                new AccountNotFoundException("Account not found with ID: " + accountId));

        List<Transaction> transactions = transactionRepository.findByAccountId(UUID.fromString(accountId));

        //return transactions.stream().map(transactionResponseMapper::toResponse).toList();

        return  transactions.stream().map(transaction -> transactionResponseMapper.toResponse(transaction)).toList();
    }
}
