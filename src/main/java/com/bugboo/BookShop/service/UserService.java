package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Order;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestCheckOutDTO;
import com.bugboo.BookShop.domain.dto.request.RequestRegisterDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateUserDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseUserDTO;
import com.bugboo.BookShop.repository.UserRepository;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, OrderService orderService, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.jwtUtils = jwtUtils;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public User convertToUser(RequestRegisterDTO registerRequestDTO) {
        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(registerRequestDTO.getPassword());
        return user;
    }

   public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean existUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmailAndRefreshToken(String email, String refreshToken) {
        return userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }
    public ResponseUserDTO convertToResponseUserDTO(User user) {
        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setId(user.getId());
        responseUserDTO.setEmail(user.getEmail());
        responseUserDTO.setName(user.getName());
        responseUserDTO.setAvatar(user.getAvatar());
        responseUserDTO.setRole(user.getRole());
        responseUserDTO.setGoogleId(user.getGoogleId());
        return responseUserDTO;
    }
    public User findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(String resetToken, Instant resetTokenExpires) {
        return userRepository.findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(resetToken, resetTokenExpires);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User handleUpdateUser(RequestUpdateUserDTO requestUpdateUserDTO) {
        User user = userRepository.findById(requestUpdateUserDTO.getId()).orElseThrow(() -> new AppException("User not found",400));
        user.setActive(requestUpdateUserDTO.isActive());
        return userRepository.save(user);
    }

    public User findByEmailOrGoogleId(String email, String googleId) {
        return userRepository.findByEmailOrGoogleId(email, googleId);
    }
}
