package com.example.sehomallapi.web.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private int productSum;
    private String email;
    private String deliveryName;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryMessage;
    private String orderStatus;
    private String createAt;
    private List<PaymentItemResponse> items;
}