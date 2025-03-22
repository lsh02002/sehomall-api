package com.example.sehomallapi.repository.heart;

import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByUserAndItem(User user, Item item);
    Boolean existsByUserIdAndItemId(Long userId, Long itemId);
    Page<Heart> findAllByUserId(Long userId, Pageable pageable);
}
