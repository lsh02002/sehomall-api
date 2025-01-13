package com.example.sehomallapi.web.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private Long id;
    private Long itemId;
    private String nickname;
    private String content;
    private Integer rating;
    private String createAt;
}
