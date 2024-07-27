package com.bugboo.BookShop.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateCartDTO {

    @NotNull(message = "cart details id is required")
    private int id;
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be greater than 0")
    private int quantity;
}
