package com.example.sehomallapi.repository.payment;

import com.example.sehomallapi.repository.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndUserId(Long id, Long userId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    List<Payment> findByUserId(Long userId);
}