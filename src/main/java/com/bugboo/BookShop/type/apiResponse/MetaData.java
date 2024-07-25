package com.bugboo.BookShop.type.apiResponse;

import lombok.Data;

@Data
public class MetaData {
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
