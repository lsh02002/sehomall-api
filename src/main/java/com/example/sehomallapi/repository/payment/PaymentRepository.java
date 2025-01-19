package com.example.sehomallapi.repository.payment;

import com.example.sehomallapi.repository.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndUser(Long id, User user);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
}