package com.example.sehomallapi.web.dto.cart;

import com.example.sehomallapi.repository.cart.Cart;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CartAllResponse {
    private final List<CartAllSearchResponse> cartAllSearchResponses;
}
