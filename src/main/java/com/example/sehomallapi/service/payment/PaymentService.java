package com.example.sehomallapi.service.payment;

import com.example.sehomallapi.config.RestPage;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.payment.*;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.BadRequestException;
import com.example.sehomallapi.service.exceptions.NotAcceptableException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.item.FileResponse;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import com.example.sehomallapi.web.dto.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
        } else if(!paymentRequest.getEmail().matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")){
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
        Payment payment = paymentRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Payment not found", id));
        List<PaymentItem> paymentItems = paymentItemRepository.findByPaymentId(id);

        return convertToPaymentResponse(payment, paymentItems);
    }

    public RestPage<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);

        return new RestPage<>(payments.map(payment->PaymentResponse.builder()
                .id(payment.getId())
                .productSum(payment.getProductSum())
                .email(payment.getEmail())
                .deliveryName(payment.getDeliveryName())
                .deliveryAddress(payment.getDeliveryAddress())
                .deliveryPhone(payment.getDeliveryPhone())
                .deliveryMessage(payment.getDeliveryMessage())
                .orderStatus(payment.getOrderStatus().toString())
                .createAt(payment.getCreateAt().toString())
                .items(payment.getPaymentItems().stream().map(this::convertToPaymentItemResponse).toList())
                .build()));

    }

    @Transactional
    public Boolean changePaymentStatus(Long userId, Long paymentId, String status) {
        try {
            Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                    .orElseThrow(() -> new NotFoundException("해당 주문사항을 찾을 수 없습니다.", null));
            payment.setOrderStatus(OrderStatus.valueOf(status));
            paymentRepository.save(payment);

            if(payment.getOrderStatus().equals(OrderStatus.CANCELED)) {
                List<PaymentItem> paymentItems = payment.getPaymentItems();

                for(PaymentItem paymentItem : paymentItems) {
                    Item item = paymentItem.getItem();

                    item.setCount(item.getCount() + paymentItem.getCount());
                    itemRepository.save(item);
                }
            }

            return true;
        } catch (NotFoundException e) {
            return false;
        }
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

        if(itemRequest.getCount() > item.getCount()){
            throw new NotAcceptableException("상품 재고는 " + item.getCount() + "개입니다.",item.getCount());
        }

        item.setCount(item.getCount() - itemRequest.getCount());
        itemRepository.save(item);

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
                .createAt(payment.getCreateAt().toString())
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
                .heartCount(item.getHeartCount())
                .userNickname(item.getUser().getNickname())
                .views(item.getViews())
                .reviewCount((long)item.getReviews().size())
                .createAt(item.getCreateAt().toString())
                .files(item.getFiles().stream().map(file ->
                        FileResponse.builder()
                                .id(file.getId())
                                .fileName(file.getFileName())
                                .fileSize(file.getFileSize())
                                .fileExtension(file.getFileExtension())
                                .fileUrl(file.getFileUrl())
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
