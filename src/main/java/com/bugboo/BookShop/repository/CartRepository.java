package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.Cart;
import com.bugboo.BookShop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUser(User currentUser);
}
