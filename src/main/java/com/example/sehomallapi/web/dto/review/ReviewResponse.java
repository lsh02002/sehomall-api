package com.example.sehomallapi.web.dto.review;

import com.example.sehomallapi.web.dto.item.FileResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private String nickname;
    private String content;
    private Integer rating;
    private String createAt;
    private List<FileResponse> files;
}
