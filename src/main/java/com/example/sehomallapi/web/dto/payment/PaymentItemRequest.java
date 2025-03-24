package com.example.sehomallapi.web.dto.payment;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItemRequest {
    private Long itemId;
    private String itemName;
    private int count;
}