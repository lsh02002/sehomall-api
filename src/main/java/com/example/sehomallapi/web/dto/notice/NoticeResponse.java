package com.example.sehomallapi.web.dto.notice;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private Long reviews;
    private String createAt;
    private String modifyAt;
}
