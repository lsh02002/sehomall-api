package com.example.sehomallapi.service.review;

import com.example.sehomallapi.repository.item.File;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.payment.*;
import com.example.sehomallapi.repository.review.Review;
import com.example.sehomallapi.repository.review.ReviewRepository;
import com.example.sehomallapi.repository.review.revieweditem.ReviewedItem;
import com.example.sehomallapi.repository.review.revieweditem.ReviewedItemRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.BadRequestException;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.service.item.FileService;
import com.example.sehomallapi.service.item.ItemService;
import com.example.sehomallapi.service.payment.PaymentService;
import com.example.sehomallapi.web.dto.item.FileResponse;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import com.example.sehomallapi.web.dto.review.ReviewRequest;
import com.example.sehomallapi.web.dto.review.ReviewResponse;
import com.example.sehomallapi.web.dto.review.ReviewedItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewedItemRepository reviewedItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final FileService fileService;
    private final ItemService itemService;

    @CachePut(key = "'all'", value = "review")
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(this::convertToReviewResponse);
    }

    @CachePut(key = "#reviewId", value = "review")
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Reivew를 찾을 수 없습니다. review id :", reviewId));

        return convertToReviewResponse(review);
    }

    @CachePut(key = "#email", value = "review")
    public Page<ReviewResponse> getReviewsByUserEmail(String email, Pageable pageable) {
        return reviewRepository.findByUserEmail(email, pageable)
                .map(this::convertToReviewResponse);

    }

    @CachePut(key = "#itemId", value = "review")
    public Page<ReviewResponse> getReviewsByItemId(Long itemId, Pageable pageable) {
        return reviewRepository.findByItemId(itemId, pageable)
                .map(this::convertToReviewResponse);
   }

   @Transactional
   @CachePut(key = "#userId", value = "review")
   public Boolean createReview(Long userId, ReviewRequest reviewRequest, List<MultipartFile> files) {
       try {
           User user = userRepository.findById(userId)
                   .orElseThrow(() -> new NotFoundException("입력하신 아이디로 회원을 찾을 수 없습니다.", userId));

           if(reviewRequest.getItemId() == null) {
               throw new NotFoundException("아이템 아이디란이 비어있습니다.", null);
           } else if (reviewRequest.getContent().trim().isEmpty()) {
               throw new NotFoundException("내용란이 비어있습니다.", null);
           } else if (reviewRequest.getRating() == null){
               throw new NotFoundException("평가란이 비어있습니다.", null);
           }

           Item item = itemRepository.findById(reviewRequest.getItemId())
                   .orElseThrow(() -> new NotFoundException("입력하신 아이템 아이디로 회원을 찾을 수 없습니다.", reviewRequest.getItemId()));

           if(reviewedItemRepository.existsByItemIdAndUserId(item.getId(), userId)) {
               throw new BadRequestException("리뷰는 구입하신 상품 하나에 한번만 올릴수 있습니다.", null);
           }

           ReviewedItemResponse itemResponse = ReviewedItemResponse.builder()
                   .id(item.getId())
                   .name(item.getName())
                   .build();

           List<ReviewedItemResponse> reviewedItemResponses = getUnReviewedItems(user.getId());
           if(!reviewedItemResponses.contains(itemResponse)) {
               throw new NotFoundException("해당 상품을 구매하신적이 없으십니다.", null);
           }

           Review review = Review.builder()
                   .content(reviewRequest.getContent())
                   .rating(reviewRequest.getRating())
                   .item(item)
                   .user(user)
                   .files(new ArrayList<>())
                   .build();

           reviewRepository.save(review);
           updateFileFromReviewRequest(review, files);

           ReviewedItem reviewedItem = ReviewedItem.builder()
                   .item(item)
                   .user(user)
                   .build();

           reviewedItemRepository.save(reviewedItem);

           return true;
       } catch (ConflictException e) {
           return false;
       }
   }

   @Transactional
   @CachePut(key = "#userId", value = "review")
    public Boolean updateReview(Long userId, Long reviewId, ReviewRequest reviewRequest, List<MultipartFile> files) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(()-> new NotFoundException("게시글을 찾을 수 없습니다.", reviewId));

            if(!Objects.equals(review.getUser().getId(), userId)){
                throw new ConflictException("사용자가 올바르지 않습니다", userId.toString());
            }

            if(reviewRequest.getContent() != null){
                review.setContent(reviewRequest.getContent());
            }
            if(reviewRequest.getRating() != null){
                review.setRating(reviewRequest.getRating());
            }
            if(reviewRequest.getItemId() != null){
                review.setItem(itemRepository.findById(reviewRequest.getItemId()).get());
            }

            reviewRepository.save(review);
            updateFileFromReviewRequest(review, files);

            return true;
        } catch (ConflictException e) {
            return false;
        }
    }

   @Transactional
   @CacheEvict(key = "#userId", value = "review")
    public Boolean deleteReview(Long userId, Long reviewId) {
       try {
           Review review = reviewRepository.findById(reviewId)
                   .orElseThrow(()-> new NotFoundException("review를 찾을 수 없습니다.", reviewId));

           if(!Objects.equals(review.getUser().getId(), userId)){
               throw new ConflictException("사용자가 올바르지 않습니다", userId.toString());
           }

           reviewRepository.delete(review);

           return true;
       } catch (ConflictException e) {
           return false;
       }
    }

    @Transactional
    @CachePut(key = "#userId", value = "review")
    public List<ReviewedItemResponse> getUnReviewedItems(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        List<ReviewedItemResponse> unReviewedItems = new ArrayList<>();

        for(Payment payment : payments){
            List<PaymentItem> paymentItems = payment.getPaymentItems();
            for(PaymentItem paymentItem : paymentItems){
                if(payment.getOrderStatus()== OrderStatus.COMPLETED && !reviewedItemRepository.existsByItemIdAndUserId(paymentItem.getItem().getId(), payment.getUser().getId())){
                    unReviewedItems.add(ReviewedItemResponse.builder()
                            .id(paymentItem.getItem().getId())
                            .name(paymentItem.getItem().getName())
                            .build());
                }
            }
        }

        return unReviewedItems.stream().distinct().toList();
    }

    private void updateFileFromReviewRequest(Review review, List<MultipartFile> files) {
        if(files == null)
            return;

        for (MultipartFile file : files) {
            review.getFiles().add(fileService.createReviewFile(file, review));
        }
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        String createAt = review.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"));

        return ReviewResponse.builder()
                .id(review.getId())
                .nickname(review.getUser().getNickname())
                .itemId(review.getItem().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createAt(createAt)
                .files(review.getFiles().stream().map(this::convertToReviewFileResponse).toList())
                .itemName(review.getItem().getName())
                .build();
    }

    private FileResponse convertToReviewFileResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .fileExtension(file.getFileExtension())
                .fileUrl(file.getFileUrl())
                .build();
    }
}
