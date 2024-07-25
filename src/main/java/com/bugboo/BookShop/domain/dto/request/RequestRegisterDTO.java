package com.bugboo.BookShop.domain.dto.request;

import com.bugboo.BookShop.domain.Review;
import com.bugboo.BookShop.domain.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class RequestRegisterDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotNull(message = "Password is required")
    @Length(min = 6, message = "Password must be at least 6 characters")
    private String password;

}
