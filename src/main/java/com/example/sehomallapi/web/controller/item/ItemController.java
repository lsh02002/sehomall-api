package com.example.sehomallapi.web.controller.item;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.item.ItemService;
import com.example.sehomallapi.web.dto.item.ItemRequest;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ItemResponse>> getItemsByCategory(@PathVariable String category, Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItemsByCategory(category, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ItemResponse>> getItemByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        Page<ItemResponse> response = itemService.getAllItemsByUser(customUserDetails.getId(), pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<ItemResponse>> getItemsByKeyword(@PathVariable String keyword, Pageable pageable) {
        return ResponseEntity.ok(itemService.getItemsByKeyword(keyword, pageable));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestPart ItemRequest itemRequest, @RequestPart(required = false) List<MultipartFile> files) {
        ItemResponse response = itemService.createItem(itemRequest, files, customUserDetails.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id, @RequestPart ItemRequest itemRequest, @RequestPart(required = false) List<MultipartFile> files) {
        ItemResponse response = itemService.updateItem(id, itemRequest, files, customUserDetails.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long id) {
        itemService.deleteItem(id, customUserDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
