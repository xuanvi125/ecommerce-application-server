package com.bugboo.BookShop.domain.dto.response;

import com.bugboo.BookShop.domain.Book;
import lombok.Data;

@Data
public class ResponseTopSellingDTO {
    ResponseBookDTO book;
    long totalQuantity;

    public ResponseTopSellingDTO(ResponseBookDTO book, long totalQuantity) {
        this.book = book;
        this.totalQuantity = totalQuantity;
    }
}
