package com.example.sehomallapi.web.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private int productSum;
    private String email;
    private String deliveryName;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryMessage;
    private List<PaymentItemRequest> items;
}