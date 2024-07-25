package com.bugboo.BookShop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @JsonIgnore
    private String password;
    private String avatar = "https://res.cloudinary.com/dv79err1w/image/upload/v1705827741/user/vettp93pj5cmz5ecdraf.jpg";
    private boolean active = true;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Review> reviews;
}
