package com.bugboo.BookShop.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RequestUpdatePasswordDTO {

    @NotNull(message = "Current password is required")
    private String currentPassword;

    @Length(min = 6, message = "Password must be at least 6 characters")
    @NotNull(message = "Password is required")
    private String password;

    @Length(min = 6, message = "Password must be at least 6 characters")
    @NotNull(message = "Confirm password is required")
    private String confirmPassword;

    public boolean isPasswordMatch() {
        return password.equals(confirmPassword);
    }
}
