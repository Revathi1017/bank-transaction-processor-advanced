package com.transaction.transprocessor.service;

import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.domain.type.TransactionType;
import com.transaction.transprocessor.dto.AccountResponse;
import com.transaction.transprocessor.dto.CreateAccountRequest;
import com.transaction.transprocessor.dto.MoneyRequest;
import com.transaction.transprocessor.dto.TransferRequest;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.exception.AccountNotFoundException;
import com.transaction.transprocessor.exception.InsufficientFundsException;
import com.transaction.transprocessor.exception.InvalidTransferException;
import com.transaction.transprocessor.mapper.AccountResponseMapper;
import com.transaction.transprocessor.repository.AccountRepository;
import com.transaction.transprocessor.service.interfaces.AccountCommandService;
import com.transaction.transprocessor.service.interfaces.AccountFactory;
import com.transaction.transprocessor.service.interfaces.AccountStrategy;
import com.transaction.transprocessor.utility.TransactionRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private static final Logger log = LoggerFactory.getLogger(AccountCommandServiceImpl.class);

    private final AccountRepository accountRepository;

    private final TransactionRecorder transactionRecorder;

    private final AccountResponseMapper accountResponseMapper;

    private final Map<AccountType, AccountStrategy> strategyMap;

    private final Map<AccountType, AccountFactory> factoryMap;

    public AccountCommandServiceImpl(AccountRepository accountRepository,
                                     AccountResponseMapper accountResponseMapper,
                                     TransactionRecorder transactionRecorder,
                                     List<AccountStrategy> strategyList,
                                     List<AccountFactory> factoryList) {
        this.accountRepository = accountRepository;
        this.accountResponseMapper = accountResponseMapper;
        this.transactionRecorder = transactionRecorder;

        //Build map from Spring injected list!
        this.strategyMap = strategyList.stream()
                .collect(Collectors.toMap(
                        AccountStrategy::supportedAccountType,
                        Function.identity()
                ));
        //Map auto populated by spring
        //SAVINGS -> SavingsAccountStrategy
        //CURRENT -> CurrentAccountStrategy
        //etc!
        // Spring finds ALL @Component strategies!
        // Adding new strategy = just add new class!
        // Constructor never changes! ✅
        // Open/Closed principle! ✅

        this.factoryMap = factoryList.stream()
                .collect(Collectors.toMap(
                        AccountFactory::getSupportedAccountType,
                        Function.identity()
                ));
    }

    //Helper to get the right strategy
    private AccountStrategy getStrategy(Account account) {
        return Optional.ofNullable(strategyMap.get(account.getAccountType()))
                .orElseThrow(() -> new UnsupportedOperationException("No strategy for: "+account.getAccountType()));
    }

    private AccountFactory getFactory(AccountType accountType) {
        return Optional.ofNullable(factoryMap.get(accountType))
                .orElseThrow(() -> new AccountNotFoundException("No factory for: "+accountType));
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest accountRequest) {

        //Step 1 - get the type of create and create it accordingly
        Account account = getFactory(accountRequest.getAccountType()).createAccountOnType(accountRequest);

        //Step 2 - save the account
        accountRepository.save(account);

        //Step 3 - return the response
        return accountResponseMapper.toResponse(account);

    }

    @Transactional
    public AccountResponse deposit(String accountID, MoneyRequest moneyRequest) {

        log.info(
                "Deposit initiated — " +
                        "accountId: {}, amount: {}",
                accountID,
                moneyRequest.getAmount());

        //Step 1 find account from DB
        Account account = accountRepository.findById(UUID.fromString(accountID))
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountID));

        log.info(
                "Account found — " +
                        "accountId: {}, " +
                        "currentBalance: {}",
                accountID,
                account.getBalance());


        //Step 2 - ask strategy to validate
        AccountStrategy strategy = getStrategy(account);
        strategy.validateDeposit(account, moneyRequest.getAmount());

        log.info(
                "Validation passed — " +
                        "accountId: {}",
                accountID);

        //Step 3 actual deposit
        account.setBalance(account.getBalance().add(moneyRequest.getAmount()));

        //Step 4 save
        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Deposit completed — " +
                        "accountId: {}, " +
                        "newBalance: {}",
                accountID,
                updatedAccount.getBalance());


        //Step 5 record transaction
        transactionRecorder.recordTransaction(updatedAccount, TransactionType.DEPOSIT, moneyRequest.getAmount());

        //Step 6 return response
        return accountResponseMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse withdraw(String accountId, MoneyRequest moneyRequest) {

        //Step 1 - Fetch account from DB
        Account account = accountRepository.findById(UUID.fromString(accountId))
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        //Step 2 - Ask strategy to validate
        AccountStrategy strategy = getStrategy(account);
        strategy.validateWithdrawal(account, moneyRequest.getAmount());

        //Step 3 - Perform withdrawal
        account.setBalance(account.getBalance().subtract(moneyRequest.getAmount()));

        //Step 4 - Save to DB
        Account updatedAccount = accountRepository.save(account);

        //Step 5 - record transaction
        transactionRecorder.recordTransaction(updatedAccount, TransactionType.WITHDRAWAL, moneyRequest.getAmount());

        //Step 6 - return the response
        return accountResponseMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse transfer(TransferRequest transferRequest) {
        if(transferRequest.getFromAccountId().equals(transferRequest.getToAccountId())) {
            throw new InvalidTransferException("Transfer within same account is not possible");
        }

        Account fromAcct = accountRepository.findById(UUID.fromString(transferRequest.getFromAccountId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + transferRequest.getFromAccountId()));

        Account toAcct = accountRepository.findById(UUID.fromString(transferRequest.getToAccountId()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + transferRequest.getToAccountId()));

        //Check if sender has sufficient money
        if(fromAcct.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available: £"
                            + fromAcct.getBalance()
                            + " Requested: £"
                            + transferRequest.getAmount());
        }

        fromAcct.setBalance(fromAcct.getBalance().subtract(transferRequest.getAmount()));

        toAcct.setBalance(toAcct.getBalance().add(transferRequest.getAmount()));

        Account updFromAcct = accountRepository.save(fromAcct);
        Account updToAcct = accountRepository.save(toAcct);

        transactionRecorder.recordTransaction(updFromAcct, TransactionType.TRANSFER_OUT, transferRequest.getAmount());

        transactionRecorder.recordTransaction(updToAcct, TransactionType.TRANSFER_IN, transferRequest.getAmount());

        return accountResponseMapper.toResponse(updFromAcct);
    }
}
