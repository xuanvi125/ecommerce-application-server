package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Review;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateReviewDTO;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.service.ReviewService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //CRUD operations for reviews
    // get all reviews
    @GetMapping
    @ApiMessage("Get all reviews successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllReviews(@Filter Specification<Review> specification, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllReviews(specification,pageable));
    }


    // get review by id
    @GetMapping("/{id}")
    @ApiMessage("Get review by id successfully")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    // add review
    @ApiMessage("Add review successfully")
    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody  Review review) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(review));
    }

    // update review
    @ApiMessage("Update review successfully")
    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody  RequestUpdateReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(reviewDTO));
    }

    // delete review
    @ApiMessage("Delete review successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
