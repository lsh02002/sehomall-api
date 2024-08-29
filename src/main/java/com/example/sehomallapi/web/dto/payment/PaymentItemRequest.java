package com.example.sehomallapi.web.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentItemRequest {
    private Long itemId;
    private int count;
}