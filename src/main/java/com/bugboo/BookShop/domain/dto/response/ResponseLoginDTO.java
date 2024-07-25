package com.bugboo.BookShop.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseLoginDTO {
    @JsonProperty("access_token")
    String accessToken;
    ResponseUserDTO user;
}
