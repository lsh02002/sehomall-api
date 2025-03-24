package com.example.sehomallapi.web.dto.payment;

import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItemResponse {
    private Long id;
    private ItemResponse item;
    private int count;
}