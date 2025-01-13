package com.example.sehomallapi.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserEmail(String email, Pageable pageable);
    Page<Review> findByItemId(Long itemId, Pageable pageable);
}
