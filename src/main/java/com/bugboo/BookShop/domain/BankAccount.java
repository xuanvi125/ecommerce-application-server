package com.bugboo.BookShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_number")
    @NotNull(message = "Account number is required")
    @Length(min = 10, max = 10, message = "Account number must be 10 characters")
    private String accountNumber;

    @Column(name = "account_name")
    @NotNull(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Balance is required")
    private int balance = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
