package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.BankAccount;
import com.bugboo.BookShop.domain.dto.request.RequestAddBankAccountDTO;
import com.bugboo.BookShop.service.BankAccountService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bank-accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    @ApiMessage("Bank accounts retrieved successfully")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @ApiMessage("Bank account added successfully")
    @PostMapping
    public ResponseEntity<BankAccount> createBankAccount(@Valid @RequestBody RequestAddBankAccountDTO requestAddBankAccountDTO) {
        return ResponseEntity.ok(bankAccountService.createBankAccount(requestAddBankAccountDTO));
    }

    @ApiMessage("Bank account deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBankAccount(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Optional.empty());
    }
}
