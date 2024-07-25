package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    User findByEmailAndRefreshToken(String email, String refreshToken);
    User findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(String resetToken, Instant resetTokenExpires);
}
