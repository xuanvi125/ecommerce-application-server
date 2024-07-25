package com.bugboo.BookShop.domain.dto.request;

import lombok.Data;

@Data
public class RequestUpdateReviewDTO {
    private int id;
    private String content;
    private double rating;
}
