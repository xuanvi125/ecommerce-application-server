package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Order;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestCheckOutDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdatePasswordDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateProfileDTO;
import com.bugboo.BookShop.domain.dto.request.RequestUpdateUserDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseLoginDTO;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseUserDTO;
import com.bugboo.BookShop.service.FileUploadService;
import com.bugboo.BookShop.service.OrderService;
import com.bugboo.BookShop.service.UserService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.apiResponse.MetaData;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final OrderService orderService;
    @Autowired
    public UserController(JwtUtils jwtUtils, UserService userService, PasswordEncoder passwordEncoder, FileUploadService fileUploadService, OrderService orderService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
        this.orderService = orderService;
    }

    @PostMapping("/users/update-password")
    @ApiMessage("Update password successfully")
    public ResponseEntity<ResponseLoginDTO> handleUpdatePassword(@Valid @RequestBody RequestUpdatePasswordDTO requestUpdatePasswordDTO){
        if (!requestUpdatePasswordDTO.isPasswordMatch()){
            throw new AppException("Password and confirm password not match",400);
        }
        String email = this.jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);

        boolean isCorrectPassword = passwordEncoder.matches(requestUpdatePasswordDTO.getCurrentPassword(), currentUser.getPassword());
        if(!isCorrectPassword){
            throw new AppException("Current password is incorrect",400);
        }

        currentUser.setPassword(passwordEncoder.encode(requestUpdatePasswordDTO.getPassword()));
        currentUser = userService.save(currentUser);
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(jwtUtils.generateToken(new UsernamePasswordAuthenticationToken(currentUser.getEmail(), currentUser.getPassword())));
        responseLoginDTO.setUser(userService.convertToResponseUserDTO(currentUser));
        return ResponseEntity.ok(responseLoginDTO);

    }

    @GetMapping("/me")
    @ApiMessage("Get current user")
    public ResponseEntity<ResponseUserDTO> handleGetCurrentUser(){
        String email = this.jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        return ResponseEntity.ok(userService.convertToResponseUserDTO(currentUser));
    }

    @PostMapping("/users/update-profile")
    @ApiMessage("Update profile successfully")
    public ResponseEntity<ResponseUserDTO> handleUpdateProfile(@Valid RequestUpdateProfileDTO request, @RequestParam(name = "file",required = false) MultipartFile file) throws IOException {
        String email = this.jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        if(file != null){
            currentUser.setAvatar(fileUploadService.uploadSingleFile(file,"avatars").get("url").toString());
        }
        if (request.getName() != null){
            currentUser.setName(request.getName());
        }
        userService.save(currentUser);
        return ResponseEntity.ok(userService.convertToResponseUserDTO(currentUser));
    }

    @GetMapping("/users/orders")
    @ApiMessage("Get orders successfully")
    public ResponseEntity<List<Order>> handleGetOrders(){
        String email = this.jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        return ResponseEntity.ok(currentUser.getOrders());
    }

    @PostMapping("/users/checkout")
    @ApiMessage("Checkout successfully")
    public ResponseEntity<Order> handleCheckout(@Valid @RequestBody RequestCheckOutDTO requestCheckOutDTO){
        String email = this.jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        return ResponseEntity.ok(orderService.checkout(currentUser,requestCheckOutDTO));
    }

    @GetMapping("/admin/users")
    @ApiMessage("Get all users successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setCurrentPage(users.getNumber() + 1);
        metaData.setTotalPages(users.getTotalPages());
        metaData.setTotalElements(users.getTotalElements());
        responsePagingResultDTO.setMetadata(metaData);
        responsePagingResultDTO.setResult(users.getContent());
        return ResponseEntity.ok(responsePagingResultDTO);
    }
    @PutMapping("/admin/users")
    @ApiMessage("Update user successfully")
    public ResponseEntity<User> updateUser(@Valid @RequestBody RequestUpdateUserDTO requestUpdateUserDTO){
        return ResponseEntity.ok(userService.handleUpdateUser(requestUpdateUserDTO));
    }
}
