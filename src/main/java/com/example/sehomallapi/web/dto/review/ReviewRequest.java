package com.example.sehomallapi.web.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewRequest {
    private String content;
    private Integer rating;
    private Long itemId;
}
