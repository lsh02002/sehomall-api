package com.example.superproject1.web.dto.payment;

import com.example.superproject1.web.dto.item.ItemResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentItemResponse {
    private Long id;
    private ItemResponse item;
    private int count;
}