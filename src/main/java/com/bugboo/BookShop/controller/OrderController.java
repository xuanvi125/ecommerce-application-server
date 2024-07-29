package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Order;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.service.OrderService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.apiResponse.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    @ApiMessage("Get all orders successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllOrders(Pageable pageable){
        Page<Order> orders = orderService.findAll(pageable);
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setCurrentPage(pageable.getPageNumber()+1);
        metaData.setTotalElements(orders.getTotalElements());
        metaData.setTotalPages(orders.getTotalPages());
        responsePagingResultDTO.setMetadata(metaData);
        responsePagingResultDTO.setResult(orders.getContent());
        return ResponseEntity.ok(responsePagingResultDTO);
    }

}
