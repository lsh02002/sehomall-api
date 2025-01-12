package com.example.sehomallapi.web.dto.cart;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAllSearchResponse {
    private Long itemId;
    private Integer count;
    private String itemName;
    private Integer price;
    private String fileUrl;
    private Boolean checked;
    private Long heartCount;
}
