package com.bugboo.BookShop.domain.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

@Embeddable
public class OrderDetailsId {
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "product_id")
    private int productId;

    public OrderDetailsId() {
    }
    public OrderDetailsId(int orderId, int productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
