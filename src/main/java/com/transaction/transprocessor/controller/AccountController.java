package com.transaction.transprocessor.controller;

import com.transaction.transprocessor.dto.*;
import com.transaction.transprocessor.service.interfaces.AccountCommandService;
import com.transaction.transprocessor.service.interfaces.AccountQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountQueryService accountQueryService;

    private final AccountCommandService accountCommandService;

    public AccountController(AccountQueryService accountQueryService, AccountCommandService accountCommandService) {
        this.accountQueryService = accountQueryService;
        this.accountCommandService = accountCommandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //201
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountCommandService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(accountQueryService.getAccountById(accountId));
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable String accountId) {
        return accountQueryService.getTransactions(accountId);
    }

    @PostMapping("/{accountId}/deposit")
    public AccountResponse deposit(@PathVariable String accountId, @Valid @RequestBody MoneyRequest request) {
        return accountCommandService.deposit(accountId, request);
    }

    @PostMapping("/{accountId}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountId, @Valid @RequestBody MoneyRequest request) {
        return accountCommandService.withdraw(accountId, request);
    }

    @PostMapping("/transfer")
    public AccountResponse transfer(@Valid @RequestBody TransferRequest request) {
        return accountCommandService.transfer(request);
    }

}
