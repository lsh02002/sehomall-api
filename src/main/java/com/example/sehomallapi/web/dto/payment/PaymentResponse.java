package com.example.sehomallapi.web.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private int productSum;
    private String email;
    private String deliveryName;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryMessage;
    private List<PaymentItemResponse> items;
}