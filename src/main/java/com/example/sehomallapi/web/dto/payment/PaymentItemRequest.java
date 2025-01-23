package com.example.sehomallapi.web.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentItemRequest {
    private Long itemId;
    private String itemName;
    private int count;
}