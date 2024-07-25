package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestRegisterDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseUserDTO;
import com.bugboo.BookShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return responseUserDTO;
    }
    public User findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(String resetToken, Instant resetTokenExpires) {
        return userRepository.findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(resetToken, resetTokenExpires);
    }

}
