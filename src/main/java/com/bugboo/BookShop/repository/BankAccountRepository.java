package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.BankAccount;
import com.bugboo.BookShop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    BankAccount findByAccountNumber(String accountNumber);

    List<BankAccount> findByUser(User user);
}
