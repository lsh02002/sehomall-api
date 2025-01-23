package com.example.sehomallapi.repository.review.revieweditem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewedItemRepository extends JpaRepository<ReviewedItem, Long> {
    Boolean existsByItemIdAndUserId(Long itemId, Long userId);
}
