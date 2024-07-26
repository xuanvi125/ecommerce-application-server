package com.bugboo.BookShop.domain;

import com.bugboo.BookShop.domain.key.OrderDetailsId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_details")
public class OrderDetails {
    @EmbeddedId
    private OrderDetailsId id;


    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Book product;
    private int quantity;
}
