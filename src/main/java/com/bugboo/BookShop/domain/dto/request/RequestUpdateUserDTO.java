package com.bugboo.BookShop.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateUserDTO {
    @NotNull(message = "id is required")
    private int id;
    @NotNull(message = "active is required")
    private boolean active;
}
