package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    User findByEmailAndRefreshToken(String email, String refreshToken);
}
