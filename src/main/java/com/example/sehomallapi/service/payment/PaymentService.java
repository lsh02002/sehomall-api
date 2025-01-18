package com.example.sehomallapi.service.payment;

import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.payment.*;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.exceptions.BadRequestException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.item.FileResponse;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import com.example.sehomallapi.web.dto.payment.PaymentItemRequest;
import com.example.sehomallapi.web.dto.payment.PaymentItemResponse;
import com.example.sehomallapi.web.dto.payment.PaymentRequest;
import com.example.sehomallapi.web.dto.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponse createPayment(Long userId, PaymentRequest paymentRequest) {
        if(paymentRequest.getDeliveryName().trim().isEmpty() || paymentRequest.getDeliveryName().length()>30) {
            throw new BadRequestException("이름은 비어있지 않고 30자리 이하여야 합니다.", paymentRequest.getDeliveryName());
        } else if(!paymentRequest.getEmail().matches(".+@.+\\..+")){
            throw new BadRequestException("이메일을 정확히 입력해주세요.", paymentRequest.getEmail());
        } else if (paymentRequest.getDeliveryName().matches("01\\d{9}")){
            throw new BadRequestException("전화번호를 이름으로 사용할수 없습니다.",paymentRequest.getDeliveryName());
        } else if(!paymentRequest.getDeliveryPhone().matches("01\\d{9}")){
            throw new BadRequestException("전화번호 형식이 올바르지 않습니다.", paymentRequest.getDeliveryPhone());
        } else if(paymentRequest.getDeliveryAddress().trim().isEmpty()) {
            throw new BadRequestException("주소는 비어있지 않아야 합니다.", paymentRequest.getDeliveryName());
        }

        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("해당 유저가 존재하지 않습니다.", null));
        Payment payment = convertToPaymentEntity(user, paymentRequest);
        paymentRepository.save(payment);

        List<PaymentItem> paymentItems = paymentRequest.getItems().stream()
                .map(itemRequest -> convertToPaymentItemEntity(itemRequest, payment))
                .collect(Collectors.toList());
        paymentItemRepository.saveAll(paymentItems);

        return convertToPaymentResponse(payment, paymentItems);
    }

    public PaymentResponse getPaymentByUserIdAndPaymentId(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("해당 유저가 존재하지 않습니다.", null));
        Payment payment = paymentRepository.findByIdAndUser(id, user).orElseThrow(() -> new NotFoundException("Payment not found", id));
        List<PaymentItem> paymentItems = paymentItemRepository.findByPaymentId(id);

        return convertToPaymentResponse(payment, paymentItems);
    }

    private Payment convertToPaymentEntity(User user, PaymentRequest paymentRequest) {
        return Payment.builder()
                .productSum(paymentRequest.getProductSum())
                .email(paymentRequest.getEmail())
                .deliveryName(paymentRequest.getDeliveryName())
                .deliveryAddress(paymentRequest.getDeliveryAddress())
                .deliveryPhone(paymentRequest.getDeliveryPhone())
                .deliveryMessage(paymentRequest.getDeliveryMessage())
                .orderStatus(OrderStatus.ORDERED)
                .user(user)
                .build();
    }

    private PaymentItem convertToPaymentItemEntity(PaymentItemRequest itemRequest, Payment payment) {
        Item item = itemRepository.findById(itemRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found", itemRequest.getItemId()));

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
                .orderStatus(payment.getOrderStatus().toString())
                .createdAt(payment.getCreateAt().toString())
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
