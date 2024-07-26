package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.dto.response.ResponseBookDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseMonthlyRevenueDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseTopSellingDTO;
import com.bugboo.BookShop.service.StatisticService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticService statisticService;

    public StatisticsController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    // get total orders
    @ApiMessage("Get total orders successfully")
    @GetMapping("/total-orders")
    public ResponseEntity<Integer> getTotalOrders() {
        return ResponseEntity.ok(statisticService.getTotalOrders());
    }

    // total revenue
    @ApiMessage("Get total revenue successfully")
    @GetMapping("/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(statisticService.getTotalRevenue());
    }

    // total product
    @ApiMessage("Get total product successfully")
    @GetMapping("/total-products")
    public ResponseEntity<Integer> getTotalProduct() {
        return ResponseEntity.ok(statisticService.getTotalProduct());
    }

    //total users
    @ApiMessage("Get total users successfully")
    @GetMapping("/total-users")
    public ResponseEntity<Integer> getTotalUsers() {
        return ResponseEntity.ok(statisticService.getTotalUsers());
    }

    @GetMapping("/monthly-revenue")
    @ApiMessage("Get monthly revenue successfully")
    public ResponseEntity<List<ResponseMonthlyRevenueDTO>> getMonthlyRevenue(Pageable pageable) {
        List<Object[]> results = statisticService.getMonthlyRevenue(pageable);
        List<ResponseMonthlyRevenueDTO> dtoList = results.stream().map(result -> {
            int month = (int) result[0];
            int year = (int) result[1];
            double totalRevenue = ((Number) result[2]).longValue();
            return new ResponseMonthlyRevenueDTO(month, year, totalRevenue);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @ApiMessage("Get top best selling products successfully")
    @GetMapping("/top-best-selling")
    public ResponseEntity<?> getTopBestSelling(Pageable pageable) {
        List<Object[]> data = statisticService.getTopBestSelling(pageable);
        List<ResponseTopSellingDTO> dtoList = data.stream().map(result -> {

            ResponseBookDTO book = new ResponseBookDTO();
            book.setId((Integer) result[0]);
            book.setAuthor((String) result[1]);
            book.setImage((String) result[2]);
            book.setName((String) result[3]);
            book.setInventory((Integer) result[4]);
            book.setDescription((String) result[5]);
            long totalQuantity = ((Number) result[6]).longValue();

            return new ResponseTopSellingDTO(book, totalQuantity);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

}
