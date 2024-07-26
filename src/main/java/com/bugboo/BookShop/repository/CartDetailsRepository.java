package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Cart;
import com.bugboo.BookShop.domain.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailsRepository extends JpaRepository<CartDetails, Integer> {
    CartDetails findByCartAndProduct(Cart cart, Book book);
    CartDetails findByIdAndCart(int id, Cart cart);
}
