package com.bugboo.BookShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"email", "active"})
    private User user;

    @NotNull(message = "Content is required")
    private String content;

    @NotNull(message = "Rating is required")
    @Range(min = 1, max = 5, message = "Rating must be between 1 and 5")
    private double rating;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;


    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
