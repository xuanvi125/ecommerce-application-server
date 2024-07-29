package com.bugboo.BookShop.domain.dto.request;

import com.bugboo.BookShop.type.constant.EnumStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateOrder {
    private int id;

    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;
}
