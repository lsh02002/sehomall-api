package com.example.sehomallapi.web.controller.item;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.item.ItemService;
import com.example.sehomallapi.web.FindUserByToken;
import com.example.sehomallapi.web.dto.item.ItemRequest;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final FindUserByToken findUserByToken;

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestBody ItemRequest itemRequest) {
        ItemResponse response = itemService.createItem(itemRequest, findUserByToken.findUser(customUserDetails));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id, @RequestBody ItemRequest itemRequest) {
        ItemResponse response = itemService.updateItem(id, itemRequest, findUserByToken.findUser(customUserDetails));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ItemResponse>> getItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        Page<ItemResponse> response = itemService.getAllItemsByUser(findUserByToken.findUser(customUserDetails), pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
