package com.bugboo.BookShop.repository;

import com.bugboo.BookShop.domain.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAll(Specification<Order> orderSpecification, Pageable pageable);

    @Query(value= "SELECT MONTH(order_date) as month, YEAR(order_date) as year, SUM(total) as totalRevenue\n" +
            "FROM orders\n" +
            "GROUP BY MONTH(order_date), YEAR(order_date)", nativeQuery = true)
    List<Object[]> getMonthlyRevenue(Pageable pageable);
}
