package com.example.sehomallapi.service.notice;

import com.example.sehomallapi.config.RestPage;
import com.example.sehomallapi.repository.notice.Notice;
import com.example.sehomallapi.repository.notice.NoticeRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.service.item.ItemService;
import com.example.sehomallapi.web.dto.notice.NoticeRequest;
import com.example.sehomallapi.web.dto.notice.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    private final ApplicationContext applicationContext;

    public RestPage<NoticeResponse> getAllNotices(Pageable pageable) {
        NoticeService self = applicationContext.getBean(NoticeService.class);

        return new RestPage<>(noticeRepository.findAll(pageable)
                .map(self::getNotice));
    }

    public RestPage<NoticeResponse> getNoticesByUserId(Long userId, Pageable pageable) {
        NoticeService self = applicationContext.getBean(NoticeService.class);

        return new RestPage<>(noticeRepository.findByUserId(userId, pageable)
                .map(self::getNotice));
    }

    @Cacheable(key = "#id", value = "notice")
    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(()->new NotFoundException("해당 게시물을 찾을 수 없습니다.", null));

        notice.setReviews(notice.getReviews() + 1);
        Notice savedNotice = noticeRepository.save(notice);

        return convertToNoticeResponse(savedNotice);
    }

    @Cacheable(key = "#notice.id", value = "notice")
    public NoticeResponse getNotice(Notice notice) {
        return convertToNoticeResponse(notice);
    }

    @Transactional
    @CachePut(key = "#result.id", value = "notice")
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
    @CachePut(key = "#result.id", value = "notice")
    public NoticeResponse updateNotice(Long userId, Long noticeId, NoticeRequest noticeRequest) {
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

            return convertToNoticeResponse(notice);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    @CacheEvict(key = "#noticeId", value = "notice")
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
