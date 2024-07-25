package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestLoginDTO;
import com.bugboo.BookShop.domain.dto.request.RequestRegisterDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseLoginDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseUserDTO;
import com.bugboo.BookShop.service.AuthService;
import com.bugboo.BookShop.service.UserService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    public AuthController(AuthService authService, JwtUtils jwtUtils, UserService userService) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiMessage("User registered successfully")
    public ResponseEntity<User> register(@Valid @RequestBody RequestRegisterDTO registerRequestDTO){
        User user = authService.register(registerRequestDTO);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    @ApiMessage("User logged in successfully")
    public ResponseEntity<ResponseLoginDTO> login(@RequestBody RequestLoginDTO requestLoginDTO){
        Authentication authentication = authService.login(requestLoginDTO);
        // create access token and refresh token
        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // save refresh token to database
        User user = userService.findByEmail(authentication.getName());
        user.setRefreshToken(refreshToken);
        userService.save(user);

        // send cookie to client
        ResponseCookie cookie = ResponseCookie.from("refresh_token",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        // create DTO and return response
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(accessToken);
        ResponseUserDTO userDTO = userService.convertToResponseUserDTO(user);
        responseLoginDTO.setUser(userDTO);
        responseLoginDTO.setUser(userDTO);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(responseLoginDTO);
    }

    @GetMapping("/logout")
    @ApiMessage("User logged out successfully")
    public ResponseEntity<Object> logout(){
        String email = jwtUtils.getCurrentUserLogin();
        User currentUser = userService.findByEmail(email);
        if (currentUser == null){
            throw new AppException("User already logout",400);
        }
        currentUser.setRefreshToken(null);
        this.userService.save(currentUser);

        // deleteCookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(null);
    }
    @GetMapping("/refresh")
    @ApiMessage("get user by refresh token")
    public ResponseEntity<Object> handleRefreshUser(@CookieValue(value = "refresh_token", defaultValue = "EMPTY") String refreshToken){
        if(refreshToken.equals("EMPTY")){
            throw new AppException("Refresh token is empty",400);
        }
        // get JWT Token From Cookie
        Jwt jwt = jwtUtils.getJwtFromToken(refreshToken);
        String email = jwt.getClaim("sub");
        User currentUser = userService.findByEmailAndRefreshToken(email, refreshToken);
        if(currentUser == null){
            throw new AppException("Refresh token is invalid",400);
        }
        // create new refresh token + access token
        String newRefreshToken = jwtUtils.generateRefreshToken(new UsernamePasswordAuthenticationToken(email, currentUser.getPassword()));
        String accessToken = jwtUtils.generateToken(new UsernamePasswordAuthenticationToken(email, currentUser.getPassword()));
        currentUser.setRefreshToken(newRefreshToken);
        userService.save(currentUser);

        ResponseCookie cookie = ResponseCookie.from("refresh_token",newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        ResponseUserDTO data = userService.convertToResponseUserDTO(currentUser);
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(accessToken);
        responseLoginDTO.setUser(data);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(responseLoginDTO);
    }



}
