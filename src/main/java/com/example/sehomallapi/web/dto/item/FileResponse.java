package com.example.sehomallapi.web.dto.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponse {
    private Long id;
    private String fileName;
    private long fileSize;
    private String fileExtension;
    private String fileUrl;
}