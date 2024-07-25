package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Review;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateReviewDTO;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.repository.BookRepository;
import com.bugboo.BookShop.repository.ReviewRepository;
import com.bugboo.BookShop.repository.UserRepository;
import com.bugboo.BookShop.type.apiResponse.MetaData;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, BookRepository bookRepository, JwtUtils jwtUtils) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.jwtUtils = jwtUtils;
    }

    public ResponsePagingResultDTO getAllReviews(Specification<Review> specification, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(specification, pageable);
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setTotalPages(reviews.getTotalPages());
        metaData.setTotalElements(reviews.getTotalElements());
        metaData.setCurrentPage(reviews.getNumber() + 1);
        responsePagingResultDTO.setMetadata(metaData);
        responsePagingResultDTO.setResult(reviews.getContent());
        return responsePagingResultDTO;
    }

    public Review getReviewById(int id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new AppException("Review not found", 404);
        }
        return review;
    }

    public Review addReview(Review review) {
        String email = jwtUtils.getCurrentUserLogin();
        User currentUser = userRepository.findByEmail(email);
        User user = userRepository.findById(review.getUser().getId()).orElse(null);
        if (user == null) {
            throw new AppException("User not found", 400);
        }

        if (currentUser.getId() != user.getId()) {
            throw new AppException("You are not allowed to review for other user", 403);
        }

        Book book = bookRepository.findById(review.getBook().getId()).orElse(null);
        if (book == null) {
            throw new AppException("Book not found", 400);
        }

        boolean exists = reviewRepository.existsByUserAndBook(currentUser, book);
        if (exists) {
            throw new AppException("You already reviewed this books", 400);
        }
        review.setUser(currentUser);
        review.setBook(book);
        double totalRating = book.getRatingAverage() * book.getRatingQuantity();
        book.setRatingQuantity(book.getRatingQuantity() + 1);
        book.setRatingAverage((totalRating + review.getRating()) / book.getRatingQuantity());
        bookRepository.save(book);
        return reviewRepository.save(review);
    }

    public Review updateReview(RequestUpdateReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewDTO.getId()).orElse(null);
        if (review == null) {
            throw new AppException("Review not found", 400);
        }

        if (!isReviewBelongToCurrentUser(review)) {
            throw new AppException("You are not allowed to update this review", 403);
        }

        Book book = bookRepository.findById(review.getBook().getId()).orElse(null);
        if (book == null) {
            throw new AppException("Book not found", 400);
        }
        double totalRating = book.getRatingAverage() * book.getRatingQuantity();
        double newRating = (totalRating - review.getRating() + reviewDTO.getRating()) / book.getRatingQuantity();
        book.setRatingAverage(newRating);
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        bookRepository.save(book);

        return reviewRepository.save(review);
    }

    public void deleteReview(int id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new AppException("Review not found", 400);
        }

        if (!isReviewBelongToCurrentUser(review)) {
            throw new AppException("You are not allowed to delete this review", 403);
        }

        Book book = review.getBook();
        double totalRating = book.getRatingAverage() * book.getRatingQuantity();
        book.setRatingQuantity(book.getRatingQuantity() - 1);
        book.setRatingAverage((totalRating - review.getRating()) / book.getRatingQuantity());
        bookRepository.save(book);
        reviewRepository.delete(review);
    }

    boolean isReviewBelongToCurrentUser(Review review) {
        String email = jwtUtils.getCurrentUserLogin();
        User currentUser = userRepository.findByEmail(email);
        int userId = currentUser.getId();
        return review.getUser().getId() == userId;
    }


}
