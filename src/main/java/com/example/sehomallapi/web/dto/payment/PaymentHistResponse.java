package com.example.sehomallapi.web.dto.payment;

import lombok.*;

import java.util.HashSet;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistResponse {
    private String email;

    @Builder.Default
    private HashSet<PaymentItemRequest> paymentItemRequests = new HashSet<>();
}
