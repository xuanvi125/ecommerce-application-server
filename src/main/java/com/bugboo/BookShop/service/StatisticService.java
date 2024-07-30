package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Order;
import com.bugboo.BookShop.domain.dto.response.ResponseTopSellingDTO;
import com.bugboo.BookShop.repository.BookRepository;
import com.bugboo.BookShop.repository.OrderDetailsRepository;
import com.bugboo.BookShop.repository.OrderRepository;
import com.bugboo.BookShop.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public StatisticService(BookRepository bookRepository, UserRepository userRepository, OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
    }


    public int getTotalOrders() {
        return (int) orderRepository.count();
    }

    public List<Object[]> getMonthlyRevenue(Pageable pageable) {
        return orderRepository.getMonthlyRevenue(pageable);
    }

    public List<Object[]> getTopBestSelling(Pageable pageable) {
        return orderDetailsRepository.getTopBestSelling(pageable);
    }

    public double getTotalRevenue() {
        return orderRepository.findAll().stream().mapToDouble(Order::getTotal).sum();
    }

    public int getTotalProduct() {
        return (int) bookRepository.count();
    }

    public int getTotalUsers() {
        return (int) userRepository.count();
    }
}
