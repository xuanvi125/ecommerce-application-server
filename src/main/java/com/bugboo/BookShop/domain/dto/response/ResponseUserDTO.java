package com.bugboo.BookShop.domain.dto.response;

import com.bugboo.BookShop.domain.Role;
import lombok.Data;

@Data
public class ResponseUserDTO {
    private Integer id;
    private String email;
    private String name;
    private String avatar;
    private Role role;
}
