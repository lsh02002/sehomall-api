package com.example.superproject1.web.controller.payment;

import com.example.superproject1.service.payment.PaymentService;
import com.example.superproject1.web.dto.payment.PaymentRequest;
import com.example.superproject1.web.dto.payment.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "결제 생성", description = "새로운 결제를 생성합니다.")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Parameter(description = "생성할 결제 정보가 포함된 요청 본문") @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @Operation(summary = "결제 조회", description = "특정 id를 가진 결제 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@Parameter(description = "경로 변수로 입력된 id값") @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}