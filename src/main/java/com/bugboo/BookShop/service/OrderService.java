package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.*;
import com.bugboo.BookShop.domain.dto.request.RequestCheckOutDTO;
import com.bugboo.BookShop.domain.key.OrderDetailsId;
import com.bugboo.BookShop.repository.CartDetailsRepository;
import com.bugboo.BookShop.repository.CartRepository;
import com.bugboo.BookShop.repository.OrderRepository;
import com.bugboo.BookShop.type.exception.AppException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailsService orderDetailsService;
    private final CartRepository cartRepository;
    private final CartDetailsRepository cartDetailsRepository;
    public OrderService(OrderRepository orderRepository, OrderDetailsService orderDetailsService, CartRepository cartRepository, CartDetailsRepository cartDetailsRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailsService = orderDetailsService;
        this.cartRepository = cartRepository;
        this.cartDetailsRepository = cartDetailsRepository;
    }

    @Transactional
    public Order checkout(User currentUser, RequestCheckOutDTO requestCheckOutDTO) {
        Order order = new Order();
        order.setUser(currentUser);
        order.setAddress(requestCheckOutDTO.getAddress());
        order.setOrderDetails(new ArrayList<>());
        Cart cart = cartRepository.findByUser(currentUser);
        List<CartDetails> cartDetails = cart.getCartDetails();
        if (cart.getSum() == 0) {
            throw new AppException("Cart is empty",400);
        }
        order = orderRepository.save(order);
        Order finalOrder = order;
        List<OrderDetails> orderDetails = cartDetails.stream().map(cartDetail -> {
            OrderDetails orderDetail = new OrderDetails();
            OrderDetailsId orderDetailsId = new OrderDetailsId(finalOrder.getId(), cartDetail.getProduct().getId());
            orderDetail.setId(orderDetailsId);
            orderDetail.setOrder(finalOrder);
            orderDetail.setProduct(cartDetail.getProduct());
            orderDetail.setQuantity(cartDetail.getQuantity());
            return orderDetailsService.save(orderDetail);
        }).toList();

        int total = cartDetails.stream().mapToInt(cartDetail -> cartDetail.getProduct().getPrice() * cartDetail.getQuantity()).sum();
        order.setTotal(total);
        cartDetailsRepository.deleteByCart(cart);
        cart.setSum(0);
        cartRepository.save(cart);
        orderRepository.save(order);
        order.setOrderDetails(orderDetails);
        return order;
    }
}
