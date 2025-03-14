package com.example.sehomallapi.web.controller.cart;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.cart.CartService;
import com.example.sehomallapi.web.dto.cart.CartAllResponse;
import com.example.sehomallapi.web.dto.cart.CartAllSearchResponse;
import com.example.sehomallapi.web.dto.cart.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartItemRequest> addCartItem(@AuthenticationPrincipal CustomUserDetails customUserDetails , @RequestBody CartItemRequest cartItemRequest) {
        Long userId = customUserDetails.getId();
        CartItemRequest response = cartService.addCartItem(userId, cartItemRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartAllResponse> findCartItems(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getId();
        CartAllResponse cartAllResponse = cartService.findCartItems(userId);

        return ResponseEntity.ok(cartAllResponse);
    }

    @PatchMapping
    public ResponseEntity<CartItemRequest> updateCartItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CartItemRequest cartItemRequest){
        Long userId = customUserDetails.getId();
        CartItemRequest response = cartService.updateCartItem(userId, cartItemRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countCartItems(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(cartService.getItemCount(customUserDetails.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id){
        cartService.deleteCartItem(customUserDetails.getId(), id);
        return ResponseEntity.ok("삭제된 아이템 목록: " + id);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCartItems(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        cartService.deleteAllCartItems(customUserDetails.getId());
        return ResponseEntity.ok("장바구니안의 아이템 모두 삭제");
    }
}
