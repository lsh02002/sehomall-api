package com.example.sehomallapi.web.controller.payment;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.payment.PaymentService;
import com.example.sehomallapi.web.dto.payment.PaymentRequest;
import com.example.sehomallapi.web.dto.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(customUserDetails.getId(), paymentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentByUserIdAndPaymentId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentByUserIdAndPaymentId(customUserDetails.getId(), id));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(customUserDetails.getId(), pageable));
    }
}