package com.example.sehomallapi.web.dto.cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CartAllResponse {
    private final List<CartAllSearchResponse> cartAllSearchResponses;
}
