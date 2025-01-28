package com.example.sehomallapi.web.controller.notice;

import com.example.sehomallapi.repository.users.userDetails.CustomUserDetails;
import com.example.sehomallapi.service.notice.NoticeService;
import com.example.sehomallapi.web.dto.notice.NoticeRequest;
import com.example.sehomallapi.web.dto.notice.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getNotices(Pageable pageable) {
        return ResponseEntity.ok(noticeService.getAllNotices(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeById(id));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<NoticeResponse>> getNoticesByUser(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(noticeService.getNoticesByUserId(userDetails.getId(), pageable));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody NoticeRequest noticeRequest) {
        return ResponseEntity.ok(noticeService.createNotice(customUserDetails.getId(), noticeRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{noticeId}")
    public ResponseEntity<Boolean> updateNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long noticeId, @RequestBody NoticeRequest noticeRequest) {
        return ResponseEntity.ok(noticeService.updateNotice(customUserDetails.getId(), noticeId, noticeRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Boolean> deleteNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.deleteNotice(customUserDetails.getId(), noticeId));
    }
}
