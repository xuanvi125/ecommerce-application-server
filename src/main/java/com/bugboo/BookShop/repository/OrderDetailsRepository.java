package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.OrderDetails;
import com.bugboo.BookShop.domain.dto.response.ResponseTopSellingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
    @Query(value = "SELECT b.id, b.author, b.image, b.name, b.inventory, b.description, SUM(od.quantity) as totalQuantity\n" +
            "FROM order_details od JOIN books b ON b.id = od.product_id\n" +
            "GROUP BY b.id\n" +
            "ORDER BY SUM(od.quantity) DESC", nativeQuery = true)
    List<Object[]> getTopBestSelling(Pageable pageable);
}
