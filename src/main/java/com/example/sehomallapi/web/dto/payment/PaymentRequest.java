package com.example.superproject1.web.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentRequest {
    private int productSum;
    private String email;
    private String deliveryName;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryMessage;
    private List<PaymentItemRequest> items;
}