package com.bugboo.BookShop.domain;

import com.bugboo.BookShop.type.constant.ConfigUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
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
    private String avatar = ConfigUtils.DEFAULT_USER_AVATAR;
    private boolean active = true;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "refresh_token",columnDefinition = "MEDIUMTEXT")
    @JsonIgnore
    private String refreshToken;

    @Column(name = "reset_password_token",columnDefinition = "MEDIUMTEXT")
    @JsonIgnore
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expires")
    @JsonIgnore
    private Instant resetPasswordTokenExpires;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<BankAccount> bankAccounts;

    @Column(name = "google_id")
    private String googleId;

}
