package com.example.sehomallapi.web.dto.review;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private String content;
    private Integer rating;
    private Long itemId;
}
