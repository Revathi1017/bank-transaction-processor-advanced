package service;

import com.transaction.transprocessor.domain.type.AccountType;
import com.transaction.transprocessor.dto.AccountResponse;
import com.transaction.transprocessor.dto.MoneyRequest;
import com.transaction.transprocessor.dto.TransferRequest;
import com.transaction.transprocessor.entity.Account;
import com.transaction.transprocessor.exception.AccountNotFoundException;
import com.transaction.transprocessor.exception.InsufficientFundsException;
import com.transaction.transprocessor.exception.InvalidTransferException;
import com.transaction.transprocessor.mapper.AccountResponseMapper;
import com.transaction.transprocessor.repository.AccountRepository;
import com.transaction.transprocessor.repository.TransactionRepository;
import com.transaction.transprocessor.service.AccountCommandServiceImpl;
import com.transaction.transprocessor.service.AccountQueryServiceImpl;
import com.transaction.transprocessor.service.interfaces.AccountFactory;
import com.transaction.transprocessor.service.interfaces.AccountStrategy;
import com.transaction.transprocessor.service.strategy.SavingsAccountStrategy;
import com.transaction.transprocessor.utility.TransactionRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountResponseMapper accountResponseMapper;

    @Mock
    private TransactionRecorder transactionRecorder;

    @Mock
    private AccountFactory accountFactory;

    @InjectMocks
    private AccountQueryServiceImpl accountQueryService;

    private AccountCommandServiceImpl accountCommandService;

    private UUID accountId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();

        AccountStrategy savingsStrategy = new SavingsAccountStrategy();

        when((accountFactory.getSupportedAccountType()))
                .thenReturn(AccountType.SAVINGS);

        accountCommandService = new AccountCommandServiceImpl(
                accountRepository, accountResponseMapper, transactionRecorder, List.of(savingsStrategy),
                List.of(accountFactory)
        );
    }

    @Test
    void shouldDepositAmountSuccessfully() {

        Account account = new Account();
        account.setAccountID(accountId);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(1000));

        MoneyRequest request = new MoneyRequest();
        request.setAmount(BigDecimal.valueOf(500));

        //If repository.findById() gets called,
        //return this fake account instead of going to DB.
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        //If repository.save() gets called,
        //return this fake account instead of going to DB.
        when(accountRepository.save(account))
                .thenReturn(account);

        AccountResponse response = new AccountResponse();
        response.setAccountId(accountId);
        response.setBalance(BigDecimal.valueOf(1500));

        when(accountResponseMapper.toResponse(account))
                .thenReturn(response);

        AccountResponse actualresponse = accountCommandService.deposit(accountId.toString(), request);

        assertEquals(BigDecimal.valueOf(1500), actualresponse.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        MoneyRequest request = new MoneyRequest();
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountCommandService.deposit(accountId.toString(), request)
        );
    }

    @Test
    void shouldWithdrawAmountSuccessfully() {

        Account account = new Account();
        account.setAccountID(accountId);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(1000));

        MoneyRequest request = new MoneyRequest();
        request.setAmount(BigDecimal.valueOf(200));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        AccountResponse response = new AccountResponse();
        response.setAccountId(accountId);
        response.setBalance(BigDecimal.valueOf(800));

        when(accountResponseMapper.toResponse(account))
                .thenReturn(response);

        AccountResponse actualresponse = accountCommandService.withdraw(accountId.toString(), request);

        assertEquals(BigDecimal.valueOf(800), actualresponse.getBalance());
    }


    @Test
    void shouldThrowExceptionWhenBalanceInsufficient() {

        Account account = new Account();
        account.setAccountID(accountId);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(100));

        MoneyRequest request = new MoneyRequest();
        request.setAmount(BigDecimal.valueOf(500));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));


        assertThrows(
                InsufficientFundsException.class,
                () -> accountCommandService.withdraw(accountId.toString(), request)
        );
    }


    @Test
    void shouldTransferAmountSuccessfully() {

        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Account sender = new Account();
        sender.setAccountID(senderId);
        sender.setAccountType(AccountType.SAVINGS);
        sender.setBalance(BigDecimal.valueOf(1000));

        Account receiver = new Account();
        receiver.setAccountID(receiverId);
        receiver.setBalance(BigDecimal.valueOf(500));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(senderId.toString());
        request.setToAccountId(receiverId.toString());
        request.setAmount(BigDecimal.valueOf(300));

        when(accountRepository.findById(senderId))
                .thenReturn(Optional.of(sender));

        when(accountRepository.findById(receiverId))
                .thenReturn(Optional.of(receiver));

        when(accountRepository.save(sender))
                .thenReturn(sender);

        when(accountRepository.save(receiver))
                .thenReturn(receiver);

        AccountResponse response = new AccountResponse();
        response.setAccountId(senderId);
        response.setBalance(BigDecimal.valueOf(700));

        when(accountResponseMapper.toResponse(sender))
                .thenReturn(response);

        AccountResponse actualresponse = accountCommandService.transfer(request);

        assertEquals(BigDecimal.valueOf(700), sender.getBalance());

        assertEquals(BigDecimal.valueOf(800), receiver.getBalance());

        assertEquals(senderId, actualresponse.getAccountId());
    }

    @Test
    void shouldRejectTransferToSameAccount() {

        UUID sameId = UUID.randomUUID();

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(sameId.toString());
        request.setToAccountId(sameId.toString());
        request.setAmount(BigDecimal.valueOf(100));

        assertThrows(
                InvalidTransferException.class,
                () -> accountCommandService.transfer(request)
        );
    }
}
