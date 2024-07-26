package com.bugboo.BookShop.domain.dto.response;

import lombok.Data;

@Data
public class ResponseMonthlyRevenueDTO {
    private Integer month;
    private Integer year;
    private double revenue;

    public ResponseMonthlyRevenueDTO(Integer month, Integer year, double totalRevenue) {
        this.month = month;
        this.year = year;
        this.revenue = totalRevenue;
    }
}
