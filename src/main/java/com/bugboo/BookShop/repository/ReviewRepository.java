package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Review;
import com.bugboo.BookShop.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    Page<Review> findAll(Specification<Review> specification, Pageable pageable);
    boolean existsByUserAndBook(User user, Book book);
}
