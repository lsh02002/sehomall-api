package com.example.sehomallapi.repository.cart;

import com.example.sehomallapi.repository.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndItem(Cart cart, Item item);

    Boolean existsByCartAndItem(Cart cart, Item item);

    List<CartItem> findByCartId(Long cartId);
    Long countByCart(Cart cart);
    void deleteByCartAndItemId(Cart cart, Long itemId);
}
