package com.example.sehomallapi.web.dto.payment;

import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentItemResponse {
    private Long id;
    private ItemResponse item;
    private int count;
}