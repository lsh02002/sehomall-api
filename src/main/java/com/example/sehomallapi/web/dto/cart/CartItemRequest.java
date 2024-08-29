package com.example.superproject1.web.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @Schema(description = "물품 아이디", example = "1")
    private Long itemId;
    @Schema(description = "물품 개수", example = "5")
    private Integer count;
}
