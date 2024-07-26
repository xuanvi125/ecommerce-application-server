package com.bugboo.BookShop.domain.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RequestAddBankAccountDTO {
    @NotNull(message = "Account number is required")
    @Length(min = 10, max = 10, message = "Account number must be 10 characters")
    private String accountNumber;

    @NotNull(message = "Account name is required")
    private String accountName;
}
