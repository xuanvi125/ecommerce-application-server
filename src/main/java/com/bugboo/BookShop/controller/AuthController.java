package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.domain.dto.request.RequestLoginDTO;
import com.bugboo.BookShop.domain.dto.request.RequestRegisterDTO;
import com.bugboo.BookShop.domain.dto.request.RequestResetPasswordDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseLoginDTO;
import com.bugboo.BookShop.domain.dto.response.ResponseUserDTO;
import com.bugboo.BookShop.repository.RoleRepository;
import com.bugboo.BookShop.service.AuthService;
import com.bugboo.BookShop.service.SendEmailService;
import com.bugboo.BookShop.service.TokenService;
import com.bugboo.BookShop.service.UserService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.constant.ConfigUtils;
import com.bugboo.BookShop.type.exception.AppException;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final SendEmailService sendEmailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    public AuthController( AuthService authService, JwtUtils jwtUtils, UserService userService, SendEmailService sendEmailService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.sendEmailService = sendEmailService;
        this.passwordEncoder = passwordEncoder;
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

    @ApiMessage("User logged out successfully")
    @GetMapping("/logout")
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
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Optional.empty());
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

    @PostMapping("/forgot-password")
    @ApiMessage("Send email to reset password successfully. Please check your email")
    public ResponseEntity<?> handleForgotPassword(@RequestBody Map<String,String> body, HttpServletRequest request) throws MessagingException, NoSuchAlgorithmException {
        String email = body.get("email");
        if (body.get("email") == null){
            throw new AppException("Email is required",400);
        }
        User user = userService.findByEmail(email);
        if(user == null){
            throw new AppException("User with this email not found",400);
        }
        // create reset password token + save to database
        String resetPasswordToken = TokenService.generateResetPasswordToken();
        String hashedToken = TokenService.hashToken(resetPasswordToken);
        user.setResetPasswordToken(hashedToken);
        user.setResetPasswordTokenExpires(TokenService.generateResetPasswordTokenExpires());
        userService.save(user);
        // send email to user

//        String scheme = request.getScheme();             // http
//        String serverName = request.getServerName();     // localhost
//        int serverPort = request.getServerPort();        // 8080


        // Construct base URL
        String baseUrl = ConfigUtils.CLIENT_URL;
        String url = baseUrl + "/reset-password?token=" + resetPasswordToken;
        Map<String,String> data = Map.of("link",url,
                "name",user.getName());
        this.sendEmailService.sendEmailWithThymeleafTemplate(email,"Reset Password",data);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/reset-password")
    @ApiMessage("Reset password successfully")
    public ResponseEntity<?> handleResetPassword(@RequestParam String token, @RequestBody RequestResetPasswordDTO requestResetPasswordDTO) throws NoSuchAlgorithmException {
        if(!requestResetPasswordDTO.isPasswordMatch()){
            throw new AppException("Password not match",400);
        }
        String hashedToken = TokenService.hashToken(token);
        User user = userService.findByResetPasswordTokenAndResetPasswordTokenExpiresAfter(hashedToken, Instant.now());
        if(user == null){
            throw new AppException("Token is invalid or expired",400);
        }
        user.setPassword(passwordEncoder.encode(requestResetPasswordDTO.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpires(null);
        userService.save(user);
        return ResponseEntity.ok().body(Optional.empty());
    }


}
