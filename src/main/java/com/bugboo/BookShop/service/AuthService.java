package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Role;
import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestLoginDTO;
import com.bugboo.BookShop.domain.dto.request.RequestRegisterDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseLoginDTO;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public AuthService(UserService userService, PasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;

    }

    public User register(RequestRegisterDTO registerRequestDTO) {
       if (userService.existUserByEmail(registerRequestDTO.getEmail())) {
            throw new AppException("User with this email already exists",400);
        }
        User user = userService.convertToUser(registerRequestDTO);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        Role role = roleService.getRoleByName("ROLE_USER");
        user.setRole(role);
        return userService.save(user);
    }

    @Transactional
    public Authentication login(RequestLoginDTO requestLoginDTO){
        if(isUnActiveUser(requestLoginDTO.getEmail())){
            throw new AppException("User is banned, contact admin for more info",400);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestLoginDTO.getEmail(), requestLoginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
       return authentication;
    }

    boolean isUnActiveUser(String email){
        User user = userService.findByEmail(email);
        return !user.isActive();
    }
}
