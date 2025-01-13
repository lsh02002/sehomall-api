package com.example.sehomallapi.web.dto.review;

import com.example.sehomallapi.web.dto.item.FileResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<FileResponse> files;
}
