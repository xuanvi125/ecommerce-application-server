package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.BankAccount;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestAddBankAccountDTO;
import com.bugboo.BookShop.repository.BankAccountRepository;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService, JwtUtils jwtUtils) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    public List<BankAccount> getAllBankAccounts() {
        String name = jwtUtils.getCurrentUserLogin();
        User user = userService.findByEmail(name);
        return bankAccountRepository.findByUser(user);

    }

    public BankAccount createBankAccount(RequestAddBankAccountDTO requestAddBankAccountDTO) {
        String name = jwtUtils.getCurrentUserLogin();
        User user = userService.findByEmail(name);

        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(requestAddBankAccountDTO.getAccountNumber());
        if (bankAccount != null) {
            throw new AppException("Bank account already exists",400);
        }

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setAccountNumber(requestAddBankAccountDTO.getAccountNumber());
        newBankAccount.setAccountName(requestAddBankAccountDTO.getAccountName());
        newBankAccount.setUser(user);
        return bankAccountRepository.save(newBankAccount);
    }

    public void deleteBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new AppException("Bank account not found",400));

        if (!isBankAccountBelongsToUser(bankAccount)) {
            throw new AppException("Bank account not belongs to user",403);
        }

        bankAccountRepository.delete(bankAccount);
    }
    boolean isBankAccountBelongsToUser(BankAccount bankAccount) {
        String name = jwtUtils.getCurrentUserLogin();
        User user = userService.findByEmail(name);
        return bankAccount.getUser().getId() == user.getId();
    }

    public BankAccount findById(int id) {
        return bankAccountRepository.findById((long) id).orElseThrow(() -> new AppException("Bank account not found",400));
    }

    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}
