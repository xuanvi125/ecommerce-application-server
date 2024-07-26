package com.bugboo.BookShop.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCheckOutDTO {
    @NotNull(message = "Address is required")
    private String address;
}
