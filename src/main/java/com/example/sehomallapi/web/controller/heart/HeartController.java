package com.example.sehomallapi.web.controller.heart;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.heart.HeartService;
import com.example.sehomallapi.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/heart")
public class HeartController {
    private final HeartService heartService;
    private final ItemService itemService;

    @PostMapping("/{id}")
    public ResponseEntity<?> insert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long itemId) {
        heartService.insert(customUserDetails.getId(), itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long itemId) {
        heartService.delete(customUserDetails.getId(), itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getMyHeartedItems(@AuthenticationPrincipal CustomUserDetails customUserDetails, Pageable pageable) {
        return ResponseEntity.ok(heartService.getMyHeartedItems(customUserDetails.getId(), pageable));
    }
}
