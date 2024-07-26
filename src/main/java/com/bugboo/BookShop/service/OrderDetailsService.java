package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.OrderDetails;
import com.bugboo.BookShop.repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;

    @Autowired
    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }

    public OrderDetails save(OrderDetails orderDetail) {
        return orderDetailsRepository.save(orderDetail);
    }
}
