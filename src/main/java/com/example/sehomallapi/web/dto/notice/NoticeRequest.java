package com.example.sehomallapi.web.dto.notice;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequest {
    private String title;
    private String content;
}
