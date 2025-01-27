package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Cart;
import com.bugboo.BookShop.domain.dto.request.RequestAddCartItemDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateCartDTO;
import com.bugboo.BookShop.service.CartService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // handle get cart
    @ApiMessage("Get cart successfully")
    @GetMapping("/users/carts")
    public ResponseEntity<Cart> getCart() {
        Cart cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    // handle add cart items
    @PostMapping("/users/carts")
    @ApiMessage("Add cart items successfully")
    public ResponseEntity<Cart> addCartItems(@Valid @RequestBody RequestAddCartItemDTO requestAddCartItemDTO) {
        Cart cart = cartService.addCartItems(requestAddCartItemDTO);
        return ResponseEntity.ok(cart);
    }

    // handle delete cart items
    @DeleteMapping("/users/carts/{id}")
    @ApiMessage("Delete cart items successfully")
    public ResponseEntity<Cart> deleteCartItems(@PathVariable int id) {
        Cart cart = cartService.deleteCartItems(id);
        return ResponseEntity.status(HttpStatus.OK).body(cart);
    }

    @DeleteMapping("/users/carts")
    @ApiMessage("Delete all cart items successfully")
    public ResponseEntity<Object> deleteAllCartItems() {
        cartService.empty();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Optional.empty());
    }

    @PutMapping("/users/carts")
    @ApiMessage("Update cart items successfully")
    public ResponseEntity<Cart> updateCartItems(@Valid @RequestBody RequestUpdateCartDTO requestUpdateCartDTO) {
        Cart cart = cartService.updateCartItems(requestUpdateCartDTO);
        return ResponseEntity.ok(cart);
    }
}
