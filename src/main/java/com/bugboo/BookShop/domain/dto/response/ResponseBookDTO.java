package com.bugboo.BookShop.domain.dto.response;

import lombok.Data;

@Data
public class ResponseBookDTO {
    private int id;
    private String author;
    private String name;
    private String image;
    private int inventory;
    private String description;
}
