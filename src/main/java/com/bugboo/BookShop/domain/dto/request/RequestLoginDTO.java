package com.bugboo.BookShop.domain.dto.request;

import lombok.Data;

@Data
public class RequestLoginDTO {
    private String email;
    private String password;
}
