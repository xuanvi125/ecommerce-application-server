package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.User;
import com.bugboo.BookShop.repository.RoleRepository;
import com.bugboo.BookShop.service.AuthService;
import com.bugboo.BookShop.service.SendEmailService;
import com.bugboo.BookShop.service.UserService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.bugboo.BookShop.type.constant.ConfigUtils;
import com.bugboo.BookShop.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Oauth2Controller {
    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public Oauth2Controller(RoleRepository roleRepository, JwtUtils jwtUtils, UserService userService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @GetMapping("/public/login/success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) {
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String avatar = principal.getAttribute("picture");
        String googleId = principal.getAttribute("sub");
        User user = userService.findByEmailOrGoogleId(email, googleId);
        if(user!= null && !user.isActive()){

            return "redirect:" + ConfigUtils.CLIENT_URL + "/login?error=Account is banned, contact admin for more information";
        }

        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAvatar(avatar);
            user.setRole(roleRepository.findByName("ROLE_USER"));
            user.setGoogleId(googleId);
            user = userService.save(user);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        user.setRefreshToken(refreshToken);
        userService.save(user);



        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpiration.intValue());
        response.addCookie(cookie);

        String redirectUrl = ConfigUtils.CLIENT_URL + "/login?token=" + accessToken;

        return "redirect:" + redirectUrl;
    }
}
