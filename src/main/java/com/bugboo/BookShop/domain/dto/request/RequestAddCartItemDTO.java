package com.bugboo.BookShop.domain.dto.request;

import com.bugboo.BookShop.domain.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestAddCartItemDTO {
    @NotNull(message = "Product cannot be null")
    Book product;
    int quantity = 1;
}
