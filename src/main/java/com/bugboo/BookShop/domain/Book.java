package com.bugboo.BookShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Author cannot be null")
    private String author;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be greater than 0")
    private int price = 0;

    @Min(value = 0, message = "Rating quantity must be greater than 0")
    @Column(name = "rating_quantity")
    private int ratingQuantity = 0;

    @Min(value = 0, message = "Rating average must be greater than 0")
    @Column(name = "rating_average")
    private double ratingAverage = 0.0;

    @NotNull(message = "Inventory cannot be null")
    @Min(value = 0, message = "Inventory must be greater than 0")
    private int inventory = 100;


    private String image = "https://res.cloudinary.com/dv79err1w/image/upload/v1705988154/product/efwphbwmbnlwqbhau7pz.png";

    private String description = "No description";
    private String publisher = "No publisher";

    @Column(name = "publish_year")
    private int publishYear = 2021;

    @NotNull(message = "Category cannot be null")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Review> reviews;

}
