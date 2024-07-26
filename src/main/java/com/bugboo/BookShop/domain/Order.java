package com.bugboo.BookShop.domain;

import com.bugboo.BookShop.type.constant.EnumStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "email", "role", "cart", "orders"})
    private User user;

    @OneToMany(mappedBy = "order")
    List<OrderDetails> orderDetails;

    private int total;
    private Instant orderDate = Instant.now();

    @Enumerated(EnumType.STRING)
    private EnumStatus status = EnumStatus.PENDING;
    String address;
}
