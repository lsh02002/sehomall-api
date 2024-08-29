package com.example.superproject1.service.payment;

import com.example.superproject1.repository.item.Item;
import com.example.superproject1.repository.item.ItemRepository;
import com.example.superproject1.repository.payment.Payment;
import com.example.superproject1.repository.payment.PaymentItem;
import com.example.superproject1.repository.payment.PaymentItemRepository;
import com.example.superproject1.repository.payment.PaymentRepository;
import com.example.superproject1.web.dto.item.FileResponse;
import com.example.superproject1.web.dto.item.ItemResponse;
import com.example.superproject1.web.dto.payment.PaymentItemRequest;
import com.example.superproject1.web.dto.payment.PaymentItemResponse;
import com.example.superproject1.web.dto.payment.PaymentRequest;
import com.example.superproject1.web.dto.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        Payment payment = convertToPaymentEntity(paymentRequest);
        paymentRepository.save(payment);

        List<PaymentItem> paymentItems = paymentRequest.getItems().stream()
                .map(itemRequest -> convertToPaymentItemEntity(itemRequest, payment))
                .collect(Collectors.toList());
        paymentItemRepository.saveAll(paymentItems);

        return convertToPaymentResponse(payment, paymentItems);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        List<PaymentItem> paymentItems = paymentItemRepository.findByPaymentId(id);

        return convertToPaymentResponse(payment, paymentItems);
    }

    private Payment convertToPaymentEntity(PaymentRequest paymentRequest) {
        return Payment.builder()
                .productSum(paymentRequest.getProductSum())
                .email(paymentRequest.getEmail())
                .deliveryName(paymentRequest.getDeliveryName())
                .deliveryAddress(paymentRequest.getDeliveryAddress())
                .deliveryPhone(paymentRequest.getDeliveryPhone())
                .deliveryMessage(paymentRequest.getDeliveryMessage())
                .build();
    }

    private PaymentItem convertToPaymentItemEntity(PaymentItemRequest itemRequest, Payment payment) {
        Item item = itemRepository.findById(itemRequest.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        return PaymentItem.builder()
                .item(item)
                .count(itemRequest.getCount())
                .payment(payment)
                .build();
    }

    private PaymentResponse convertToPaymentResponse(Payment payment, List<PaymentItem> paymentItems) {
        List<PaymentItemResponse> itemResponses = paymentItems.stream()
                .map(this::convertToPaymentItemResponse)
                .collect(Collectors.toList());

        return PaymentResponse.builder()
                .id(payment.getId())
                .productSum(payment.getProductSum())
                .email(payment.getEmail())
                .deliveryName(payment.getDeliveryName())
                .deliveryAddress(payment.getDeliveryAddress())
                .deliveryPhone(payment.getDeliveryPhone())
                .deliveryMessage(payment.getDeliveryMessage())
                .items(itemResponses)
                .build();
    }

    private PaymentItemResponse convertToPaymentItemResponse(PaymentItem paymentItem) {
        Item item = paymentItem.getItem();
        ItemResponse itemResponse = ItemResponse.builder()
                .id(item.getId())
                .count(item.getCount())
                .price(item.getPrice())
                .size(item.getSize())
                .careGuide(item.getCareGuide())
                .name(item.getName())
                .description(item.getDescription())
                .category(item.getCategory())
                .deliveryFee(item.getDeliveryFee())
                .files(item.getFiles().stream().map(file ->
                        FileResponse.builder()
                                .id(file.getId())
                                .fileName(file.getFileName())
                                .fileSize(file.getFileSize())
                                .fileExtension(file.getFileExtension())
                                .build()
                ).collect(Collectors.toList()))
                .build();

        return PaymentItemResponse.builder()
                .id(paymentItem.getId())
                .item(itemResponse)
                .count(paymentItem.getCount())
                .build();
    }
}
