package com.bugboo.BookShop.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateBankAccount {
    @NotNull(message = "Id is required")
    private int id;

    @NotNull(message = "Account number is required")
    @Min(value= 10000, message = "Balance must be greater than 10000")
    private int balance;
}
