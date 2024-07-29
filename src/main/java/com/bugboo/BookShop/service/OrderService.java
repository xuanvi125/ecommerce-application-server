package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.*;
import com.bugboo.BookShop.domain.dto.request.RequestCheckOutDTO;
import com.bugboo.BookShop.domain.key.OrderDetailsId;
import com.bugboo.BookShop.repository.BankAccountRepository;
import com.bugboo.BookShop.repository.CartDetailsRepository;
import com.bugboo.BookShop.repository.CartRepository;
import com.bugboo.BookShop.repository.OrderRepository;
import com.bugboo.BookShop.type.exception.AppException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailsService orderDetailsService;
    private final CartRepository cartRepository;
    private final CartDetailsRepository cartDetailsRepository;
    private final BankAccountRepository bankAccountRepository;
    public OrderService(OrderRepository orderRepository, OrderDetailsService orderDetailsService, CartRepository cartRepository, CartDetailsRepository cartDetailsRepository, BankAccountRepository bankAccountRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailsService = orderDetailsService;
        this.cartRepository = cartRepository;
        this.cartDetailsRepository = cartDetailsRepository;
        this.bankAccountRepository = bankAccountRepository;
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

        BankAccount bankAccount = bankAccountRepository.findById(Long.valueOf(requestCheckOutDTO.getBankAccount().getId())).orElseThrow(() -> new AppException("Bank account not found",400));
        if (bankAccount.getUser().getId() != currentUser.getId()) {
            throw new AppException("Bank account not belongs to user",403);
        }
        if (bankAccount.getBalance() < total) {
            throw new AppException("Insufficient balance",400);
        }
        bankAccount.setBalance(bankAccount.getBalance() - total);
        bankAccountRepository.save(bankAccount);

        order.setTotal(total);
        cartDetailsRepository.deleteByCart(cart);
        cart.setSum(0);
        cartRepository.save(cart);
        orderRepository.save(order);
        order.setOrderDetails(orderDetails);
        return order;
    }


    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
