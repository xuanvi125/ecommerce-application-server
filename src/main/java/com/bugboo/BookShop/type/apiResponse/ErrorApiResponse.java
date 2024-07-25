package com.bugboo.BookShop.type.apiResponse;

import lombok.Data;

@Data
public class ErrorApiResponse {
    private String status;
    private String message;
}
