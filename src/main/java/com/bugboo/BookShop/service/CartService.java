package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Cart;
import com.bugboo.BookShop.domain.CartDetails;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestAddCartItemDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateCartDTO;
import com.bugboo.BookShop.repository.CartRepository;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final BookService bookService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CartDetailsService cartDetailsService;

    @Autowired
    public CartService(CartRepository cartRepository, BookService bookService, UserService userService, JwtUtils jwtUtils, CartDetailsService cartDetailsService) {
        this.cartRepository = cartRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.cartDetailsService = cartDetailsService;
    }

    public Cart getCart() {
        String email = jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        if (currentUser == null) {
            throw new AppException("Please login to perform this action", 400);
        }
        Cart cart = cartRepository.findByUser(currentUser);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(currentUser);
            cart.setCartDetails(new ArrayList<>());
            return cartRepository.save(cart);
        }
        return cart;
    }

    public Cart addCartItems(RequestAddCartItemDTO requestAddCartItemDTO) {
        Cart cart = this.getCart();
        Book book = bookService.findById(requestAddCartItemDTO.getProduct().getId());
        if (book == null) {
            throw new AppException("Product to add not found", 400);
        }
        CartDetails cartDetails = cartDetailsService.findByCartAndProduct(cart, book);
        if (cartDetails == null) {
            cartDetails = new CartDetails();
            cartDetails.setCart(cart);
            cartDetails.setProduct(book);
            cartDetails.setQuantity(requestAddCartItemDTO.getQuantity());
            cartDetails.setPrice(book.getPrice());
            cartDetailsService.save(cartDetails);
            cart.setSum(cart.getSum() + 1);
            cart.getCartDetails().add(cartDetails);
        } else {
            cartDetails.setQuantity(cartDetails.getQuantity() + requestAddCartItemDTO.getQuantity());
        }
        return cartRepository.save(cart);
    }

    public Cart deleteCartItems(int id) {
        Cart cart = this.getCart();
        CartDetails cartDetails = cartDetailsService.findByIdAndCart(id, cart);
        if (cartDetails == null) {
            throw new AppException("Cart item not found or not belong to current user", 400);
        }
        cartDetailsService.delete(cartDetails);
        cart.setSum(cart.getSum() - 1);
        return cartRepository.save(cart);
    }

    @Transactional
    public void empty() {
        Cart cart = this.getCart();
        cartDetailsService.deleteByCart(cart);
        cart.setSum(0);
        cartRepository.save(cart);
    }

    public Cart findCartByUser(User currentUser) {
        return cartRepository.findByUser(currentUser);
    }


    public Cart updateCartItems(RequestUpdateCartDTO requestUpdateCartDTO) {
        Cart cart = this.getCart();
        CartDetails cartDetails = cartDetailsService.findByIdAndCart(requestUpdateCartDTO.getId(), cart);
        if (cartDetails == null) {
            throw new AppException("Cart item not found or not belong to current user", 400);
        }
        cartDetails.setQuantity(requestUpdateCartDTO.getQuantity());
        return cartRepository.save(cart);
    }
}
