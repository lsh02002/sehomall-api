package com.example.sehomallapi.service.notice;

import com.example.sehomallapi.repository.notice.Notice;
import com.example.sehomallapi.repository.notice.NoticeRepository;
import com.example.sehomallapi.repository.review.Review;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.service.users.UserService;
import com.example.sehomallapi.web.dto.notice.NoticeRequest;
import com.example.sehomallapi.web.dto.notice.NoticeResponse;
import com.example.sehomallapi.web.dto.review.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @CachePut(key = "'all'", value = "notice")
    public Page<NoticeResponse> getAllNotices(Pageable pageable) {
        List<NoticeResponse> noticeResponses = noticeRepository.findAll(pageable).stream()
                .map(this::convertToNoticeResponse).toList();

        return new PageImpl<>(noticeResponses, pageable, noticeResponses.size());
    }

    @CachePut(key = "#id", value = "notice")
    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(()->new NotFoundException("해당 게시물을 찾을 수 없습니다.", null));

        notice.setReviews(notice.getReviews() + 1);
        Notice savedNotice = noticeRepository.save(notice);

        return convertToNoticeResponse(savedNotice);
    }

    @CachePut(key = "#userId", value = "notice")
    public Page<NoticeResponse> getNoticesByUserId(Long userId, Pageable pageable) {
        List<NoticeResponse> noticeResponses = noticeRepository.findByUserId(userId, pageable).stream()
                .map(this::convertToNoticeResponse).toList();

        return new PageImpl<>(noticeResponses, pageable, noticeResponses.size());
    }

    @Transactional
    @CachePut(key = "#userId", value = "notice")
    public NoticeResponse createNotice(Long userId, NoticeRequest noticeRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("해당 회원을 찾을 수 없습니다.", null));

        if(noticeRequest.getTitle() == null || noticeRequest.getTitle().trim().isEmpty()) {
            throw new NotFoundException("게시글 제목란이 비어있습니다.", null);
        } else if (noticeRequest.getContent() == null || noticeRequest.getContent().trim().isEmpty()) {
            throw new NotFoundException("게시글 내용란이 비어있습니다.", null);
        }

        Notice notice = convertToNoticeEntity(user, noticeRequest);
        Notice savedNotice = noticeRepository.save(notice);

        return convertToNoticeResponse(savedNotice);
    }

    @Transactional
    @CachePut(key = "#userId", value = "notice")
    public Boolean updateNotice(Long userId, Long noticeId, NoticeRequest noticeRequest) {
        try {
            Notice notice = noticeRepository.findById(noticeId)
                    .orElseThrow(()-> new NotFoundException("게시글을 찾을 수 없습니다.", noticeId));

            if(!Objects.equals(notice.getUser().getId(), userId)){
                throw new ConflictException("사용자가 올바르지 않습니다", userId.toString());
            }

            if(noticeRequest.getTitle() != null){
                notice.setTitle(noticeRequest.getTitle());
            }

            if(noticeRequest.getContent() != null){
                notice.setContent(noticeRequest.getContent());
            }

            noticeRepository.save(notice);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    @CacheEvict(key = "#userId", value = "notice")
    public Boolean deleteNotice(Long userId, Long noticeId) {
        try {
            Notice notice = noticeRepository.findById(noticeId)
                    .orElseThrow(()-> new NotFoundException("해당 게시글 찾을 수 없습니다.", noticeId));

            if(!Objects.equals(notice.getUser().getId(), userId)){
                throw new ConflictException("사용자가 올바르지 않습니다", userId.toString());
            }

            noticeRepository.delete(notice);

            return true;
        } catch (ConflictException e) {
            return false;
        }
    }

    private NoticeResponse convertToNoticeResponse(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .nickname(notice.getUser().getNickname())
                .reviews(notice.getReviews())
                .createAt(notice.getCreateAt().toString())
                .modifyAt(notice.getModifyAt().toString())
                .build();
    }

    private Notice convertToNoticeEntity(User user, NoticeRequest noticeRequest) {
        return Notice.builder()
                .title(noticeRequest.getTitle())
                .content(noticeRequest.getContent())
                .reviews(0L)
                .user(user)
                .build();
    }
}
