package com.example.sehomallapi.repository.item;

import com.example.sehomallapi.repository.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByUser(User user, Pageable pageable);
    Optional<Item> findByIdAndUserId(Long id, Long userId);
    Page<Item> findByCategory(String category, Pageable pageable);
    Boolean existsByName(String name);
    @Query("select i from Item i where i.name like %:keyword%")
    Page<Item> findByKeyword(String keyword, Pageable pageable);
}
