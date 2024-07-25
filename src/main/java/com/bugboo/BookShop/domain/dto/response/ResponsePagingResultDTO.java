package com.bugboo.BookShop.domain.dto.response;

import com.bugboo.BookShop.type.apiResponse.MetaData;
import lombok.Data;

@Data
public class ResponsePagingResultDTO {
    MetaData metadata;
    Object result;
}
