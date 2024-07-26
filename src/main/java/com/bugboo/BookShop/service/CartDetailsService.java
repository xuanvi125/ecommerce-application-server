package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Cart;
import com.bugboo.BookShop.domain.CartDetails;
import com.bugboo.BookShop.repository.CartDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartDetailsService {
    private final CartDetailsRepository cartDetailsRepository;

    @Autowired
    public CartDetailsService(CartDetailsRepository cartDetailsRepository) {
        this.cartDetailsRepository = cartDetailsRepository;
    }

    public CartDetails findByCartAndProduct(Cart cart, Book book) {
        return cartDetailsRepository.findByCartAndProduct(cart, book);
    }

    public CartDetails save(CartDetails cartDetails) {
        return cartDetailsRepository.save(cartDetails);
    }

    public CartDetails findById(int id) {
        return cartDetailsRepository.findById(id).orElse(null);
    }

    public void delete(CartDetails cartDetails) {
        cartDetailsRepository.delete(cartDetails);
    }
    public CartDetails findByIdAndCart(int id, Cart cart) {
        return cartDetailsRepository.findByIdAndCart(id, cart);
    }
}
